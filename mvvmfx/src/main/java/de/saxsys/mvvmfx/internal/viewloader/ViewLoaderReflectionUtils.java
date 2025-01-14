/*******************************************************************************
 * Copyright 2015 Alexander Casall, Manuel Mauky, Sven Lechner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.saxsys.mvvmfx.internal.viewloader;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.Initialize;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.internal.ContextImpl;
import javafx.beans.value.ObservableBooleanValue;
import net.jodah.typetools.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class encapsulates reflection related utility operations specific for
 * loading of views.
 * 
 * @author manuel.mauky
 */
public class ViewLoaderReflectionUtils {

    /**
     * Returns the {@link java.lang.reflect.Field} of the viewModel for a given
     * view type and viewModel type. If there is no annotated field for the
     * viewModel in the view the returned Optional will be empty.
     *
     * @param viewType
     *            the type of the view
     * @param viewModelType
     *            the type of the viewModel
     * @return an Optional that contains the Field when the field exists.
     */
    public static Optional<Field> getViewModelField(Class<? extends View> viewType, Class<?> viewModelType) {
        List<Field> allViewModelFields = getViewModelFields(viewType);

        if (allViewModelFields.isEmpty()) {
            return Optional.empty();
        }

        if (allViewModelFields.size() > 1) {
            throw new RuntimeException("The View <" + viewType + "> may only define one viewModel but there were <"
                    + allViewModelFields.size() + "> viewModel fields with the @InjectViewModel annotation!");
        }

        Field field = allViewModelFields.get(0);

        if (!ViewModel.class.isAssignableFrom(field.getType())) {
            throw new RuntimeException("The View <" + viewType
                    + "> has a field annotated with @InjectViewModel but the type of the field doesn't implement the 'ViewModel' interface!");
        }

        if (!field.getType().isAssignableFrom(viewModelType)) {
            throw new RuntimeException("The View <" + viewType
                    + "> has a field annotated with @InjectViewModel but the type of the field doesn't match the generic ViewModel type of the View class. "
                    + "The declared generic type is <" + viewModelType + "> but the actual type of the field is <"
                    + field.getType() + ">.");
        }

        return Optional.of(field);
    }

    public static List<Field> getScopeFields(Class<?> viewModelType) {
        final List<Field> allScopeFields = getFieldsWithAnnotation(viewModelType, InjectScope.class);

        allScopeFields.stream().forEach(field -> {
            if (!Scope.class.isAssignableFrom(field.getType())) {
                throw new RuntimeException("The ViewModel <" + viewModelType
                        + "> has a field annotated with @InjectScope but the type of the field doesn't implement the 'Scope' interface!");
            }
        });

        return allScopeFields;
    }

    private static Optional<Field> getContextField(Class<? extends View> viewType) {
        List<Field> allViewModelFields = getContextFields(viewType);

        if (allViewModelFields.isEmpty()) {
            return Optional.empty();
        }

        if (allViewModelFields.size() > 1) {
            throw new RuntimeException("The View <" + viewType + "> may only define one Context but there were <"
                    + allViewModelFields.size() + "> Context fields with the @InjectContext annotation!");
        }

        Field field = allViewModelFields.get(0);

        if (!field.getType().isAssignableFrom(Context.class)) {
            throw new RuntimeException("The View <" + viewType
                    + "> has a field annotated with @InjectContext but the type of the field doesn't match the type Context. "
                    + "The actual type of the field is <" + field.getType() + ">.");
        }

        return Optional.of(field);
    }

    private static List<Field> getContextFields(Class<? extends View> viewType) {
        return getFieldsWithAnnotation(viewType, InjectContext.class);
    }

    /**
     * Returns a list of all {@link Field}s of ViewModels for a given view type
     * that are annotated with {@link InjectViewModel}.
     * 
     * @param viewType
     *            the type of the view.
     * @return a list of fields.
     */
    public static List<Field> getViewModelFields(Class<? extends View> viewType) {
        return getFieldsWithAnnotation(viewType, InjectViewModel.class);
    }

    private static <T, A extends Annotation> List<Field> getFieldsWithAnnotation(Class<T> classType,
            Class<A> annotationType) {
        return ReflectionUtils.getFieldsFromClassHierarchy(classType)
                .stream()
                .filter(field -> field.isAnnotationPresent(annotationType))
                .collect(Collectors.toList());
    }

    /**
     * This method is used to get the ViewModel instance of a given
     * view/codeBehind.
     *
     * @param view
     *            the view instance where the viewModel will be looked for.
     * @param <ViewType>
     *            the generic type of the View
     * @param <ViewModelType>
     *            the generic type of the ViewModel
     * @return the ViewModel instance or null if no viewModel could be found.
     */
    @SuppressWarnings("unchecked")
    public static <ViewType extends View<? extends ViewModelType>, ViewModelType extends ViewModel> ViewModelType getExistingViewModel(
            ViewType view) {
        final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());
        Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModelType);
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            return ReflectionUtils.accessMember(field, () -> (ViewModelType) field.get(view),
                    "Can't get the viewModel of type <" + viewModelType + ">");
        } else {
            return null;
        }
    }

    /**
     * Injects the given viewModel instance into the given view. The injection
     * will only happen when the class of the given view has a viewModel field
     * that fulfills all requirements for the viewModel injection (matching
     * types, no viewModel already existing ...).
     *
     * @param view
     * @param viewModel
     */
    public static void injectViewModel(final View view, ViewModel viewModel) {
        if (viewModel == null) {
            return;
        }
        final Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModel.getClass());
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            ReflectionUtils.accessMember(field, () -> {
                Object existingViewModel = field.get(view);
                if (existingViewModel == null) {
                    field.set(view, viewModel);
                }
            }, "Can't inject ViewModel of type <" + viewModel.getClass() + "> into the view <" + view + ">");
        }
    }

    /**
     * This method is used to create and inject the ViewModel for a given View
     * instance.
     * 
     * The following checks are done:
     * <ul>
     * <li>Check whether the View class specifies a ViewModel type as generic
     * type.</li>
     * <li>Check whether the View has a field with a matching ViewModel type and
     * the annotation {@link InjectViewModel}.</li>
     * 
     * <li>Check whether field in the view instance already contains a ViewModel
     * instance. In this case nothing will happen to the existing ViewModel
     * instance.</li>
     * 
     * </ul>
     * 
     * If a suitable field was found a new ViewModel instance will be created
     * and injected into the field. After that the given Consumer function will
     * be applied with the injected ViewModel instance as argument.
     * 
     * @param view
     *            the view instance.
     * @param <V>
     *            the generic type of the View.
     * @param <VM>
     *            the generic type of the ViewModel.
     * @param newVmConsumer
     *            a Consumer function that is applied when a new ViewModel
     *            instance is created.
     * 
     * @throws RuntimeException
     *             if there is a ViewModel field in the View with the
     *             {@link InjectViewModel} annotation whose type doesn't match
     *             the generic ViewModel type from the View class.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View<? extends VM>, VM extends ViewModel> void createAndInjectViewModel(final V view,
            Consumer<ViewModel> newVmConsumer) {
        final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());

        if (viewModelType == ViewModel.class) {
            // if no viewModel can be created, we have to check if the user has
            // tried to inject a ViewModel
            final List<Field> viewModelFields = ViewLoaderReflectionUtils.getViewModelFields(view.getClass());

            if (!viewModelFields.isEmpty()) {
                throw new RuntimeException("The given view of type <" + view.getClass()
                        + "> has no generic viewModel type declared but tries to inject a viewModel.");
            }
            return;
        }
        if (viewModelType == TypeResolver.Unknown.class) {
            return;
        }

        final Optional<Field> fieldOptional = getViewModelField(view.getClass(), viewModelType);
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();

            ReflectionUtils.accessMember(field, () -> {
                Object existingViewModel = field.get(view);

                if (existingViewModel == null) {
                    final Object newViewModel = DependencyInjector.getInstance().getInstanceOf(viewModelType);

                    field.set(view, newViewModel);

                    newVmConsumer.accept((ViewModel) newViewModel);
                }
            }, "Can't inject ViewModel of type <" + viewModelType + "> into the view <" + view + ">");

        }
    }

    static void createAndInjectScopes(Object viewModel, ContextImpl context) {

        // FIXME CLEANUP!!!
        Class<? extends Object> viewModelClass = viewModel.getClass();

        for (Annotation annotation : viewModelClass.getDeclaredAnnotations()) {
            if (annotation.annotationType().isAssignableFrom(ScopeProvider.class)) {
                ScopeProvider provider = (ScopeProvider) annotation;
                Class<? extends Scope>[] scopes = getScopesFromProvider(provider, viewModelClass);
                for (int i = 0; i < scopes.length; i++) {
                    Class<? extends Scope> scopeType = scopes[i];
                    // Overrides existing scopes!!!!
                    context.addScopeToContext(DependencyInjector.getInstance().getInstanceOf(scopeType));
                }
            }
        }

        // Inject
        List<Field> scopeFields = getScopeFields(viewModel.getClass());

        scopeFields.forEach(scopeField -> {
            ReflectionUtils.accessMember(scopeField, () -> injectScopeIntoField(scopeField, viewModel, context),
                    "Can't inject Scope into ViewModel <" + viewModel.getClass() + ">");
        });
    }

    private static Class<? extends Scope>[] getScopesFromProvider(final ScopeProvider scopeProvider, final Class<?> aViewModelClass) {
        Class<? extends Scope>[] scopes = scopeProvider.value();
        
        if (scopes.length == 0) {
            scopes = scopeProvider.scopes();
        }
        
        if (scopes.length == 0) {
            final String message = String.format("The scope provider '%s' has to provide at least one scope.", aViewModelClass.getCanonicalName());
            throw new IllegalArgumentException(message);
        }

        return scopes;
    }

    public static void injectContext(View codeBehind, ContextImpl context) {

        Optional<Field> contextField = getContextField(codeBehind.getClass());

        if (contextField.isPresent()) {
            Field field = contextField.get();
            ReflectionUtils.accessMember(field, () -> {
                field.set(codeBehind, context);
            }, "Can't inject Context into the view <" + codeBehind + ">");
        }
    }

    static Object injectScopeIntoField(Field scopeField, Object viewModel, ContextImpl context)
            throws IllegalAccessException {
        Class<? extends Scope> scopeType = (Class<? extends Scope>) scopeField.getType();

        // FIXME

        final InjectScope[] annotations = scopeField.getAnnotationsByType(InjectScope.class);

        if (annotations.length != 1) {
            throw new RuntimeException("A field to inject a Scope into should have exactly one @InjectScope annotation "
                    + "but the viewModel <" + viewModel + "> has a field that violates this rule.");
        }

        Object newScope = context.getScope(scopeType);

        if (newScope == null) {
            // TODO Modify Stacktrace to get the Injectionpoint of the Scope
            throw new IllegalStateException(
                    "A scope was requested but no @ScopeProvider found in the hierarchy. Declare it like this: @ScopeProvider("
                            + scopeType.getName() + ".class )");
        }

        if (!newScope.getClass().equals(scopeType)) {
            throw new IllegalStateException("something went wrong...");
        }

        scopeField.set(viewModel, newScope);

        return newScope;
    }

    /**
     * Creates a viewModel instance for a View type. The type of the view is
     * determined by the given view instance.
     *
     * For the creation of the viewModel the {@link DependencyInjector} is used.
     * 
     * @param view
     *            the view instance that is used to find out the type of the
     *            ViewModel
     * @param <ViewType>
     *            the generic view type
     * @param <ViewModelType>
     *            the generic viewModel type
     * @return the viewModel instance or <code>null</code> if the viewModel type
     *         can't be found or the viewModel can't be created.
     */
    @SuppressWarnings("unchecked")
    public static <ViewType extends View<? extends ViewModelType>, ViewModelType extends ViewModel> ViewModelType createViewModel(
            ViewType view) {
        final Class<?> viewModelType = TypeResolver.resolveRawArgument(View.class, view.getClass());
        if (viewModelType == ViewModel.class) {
            return null;
        }
        if (TypeResolver.Unknown.class == viewModelType) {
            return null;
        }
        return (ViewModelType) DependencyInjector.getInstance().getInstanceOf(viewModelType);
    }

    /**
     * If a ViewModel has a method annotated with {@link Initialize}
     * or method with the signature <code>public void initialize()</code>
     * it will be invoked. If no such method is available nothing happens.
     *
     * @param viewModel
     *            the viewModel that's initialize method (if available) will be
     *            invoked.
     * @param <ViewModelType>
     *            the generic type of the ViewModel.
     */
    public static <ViewModelType extends ViewModel> void initializeViewModel(ViewModelType viewModel) {
        if (viewModel == null) {
            return;
        }

        final Collection<Method> initializeMethods = getInitializeMethods(viewModel.getClass());

        initializeMethods.forEach(initMethod -> {
			// if there is a @PostConstruct annotation, throw an exception to prevent double injection
            final boolean postConstructPresent = Arrays.stream(initMethod.getAnnotations())
                    .map(Annotation::annotationType)
                    .map(Class::getName)
                    .anyMatch("jakarta.annotation.PostConstruct"::equals);

            if(postConstructPresent) {
				throw new IllegalStateException(String.format("initialize method of ViewModel [%s] is annotated with @PostConstruct. " +
						"This will lead to unexpected behaviour and duplicate initialization. " +
						"Please rename the method or remove the @PostConstruct annotation. " +
						"See mvvmFX wiki for more details: " +
						"https://github.com/sialcasa/mvvmFX/wiki/Dependency-Injection#lifecycle-postconstruct", viewModel));
			}

			ReflectionUtils.accessMember(initMethod, () -> initMethod.invoke(viewModel), "mvvmFX wasn't able to call the initialize method of ViewModel [" + viewModel + "].");
		});
    }

	/**
	 * Returns a collection of {@link Method}s that represent initializer methods.
	 * A method is an "initializer method" if it either: <br/>
	 * <ol>
	 *     <li>has a signature of "public void initialize()"</li>
	 *     <li>is annotated with {@link Initialize}</li>
	 * </ol>
	 */
	private static Collection<Method> getInitializeMethods(Class<?> classType) {
        final List<Method> initializeMethods = new ArrayList<>();

        Arrays.stream(classType.getMethods())
                .filter(method -> "initialize".equals(method.getName()))
                .filter(method -> void.class.equals(method.getReturnType()))
                .filter(method -> method.getParameterCount() == 0)
                .forEach(initializeMethods::add);

		Arrays.stream(classType.getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(Initialize.class))
				.forEach(initializeMethods::add);

		return initializeMethods;
	}


    /**
     * This method adds listeners for the {@link SceneLifecycle}.
     */
    static void addSceneLifecycleHooks(ViewModel viewModel, ObservableBooleanValue viewInSceneProperty) {
        if(viewModel != null) {

            if(viewModel instanceof SceneLifecycle) {
                SceneLifecycle lifecycleViewModel = (SceneLifecycle) viewModel;

                PreventGarbageCollectionStore.getInstance().put(viewInSceneProperty);

                viewInSceneProperty.addListener((observable, oldValue, newValue) -> {
					if(newValue) {
						lifecycleViewModel.onViewAdded();
					} else {
						lifecycleViewModel.onViewRemoved();
                        PreventGarbageCollectionStore.getInstance().remove(viewInSceneProperty);
					}
				});
            }
        }
    }

    /**
     * This method is used to check if the given View instance has one or more fields that try to inject a scope
     * with the {@link InjectScope} annotation.
     * This is a violation of the mvvm-pattern and therefore this method will throw an exception with a message describing the
     * error.
     */
    static void checkScopesInView(View codeBehind) {
        List<Field> scopeFields = ReflectionUtils.getFieldsWithAnnotation(codeBehind, InjectScope.class);

        if(!scopeFields.isEmpty()) {
            throw new IllegalStateException("The view class [" + codeBehind.getClass().getSimpleName() + "] tries to inject a Scope with " +
                    "@InjectScope. This would be a violation of the mvvm pattern. Scopes are only supported in ViewModels.");
        }

    }
}
