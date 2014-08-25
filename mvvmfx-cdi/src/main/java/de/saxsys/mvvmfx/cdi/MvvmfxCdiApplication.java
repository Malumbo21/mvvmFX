/*******************************************************************************
 * Copyright 2013 Alexander Casall, Manuel Mauky
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
package de.saxsys.mvvmfx.cdi;

import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.cdi.internal.CdiInjector;
import de.saxsys.mvvmfx.cdi.internal.MvvmfxProducer;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;


/**
 * This class has to be extended by the user to build a javafx application powered by CDI.
 *
 * @author manuel.mauky
 */
public abstract class MvvmfxCdiApplication extends Application {


	private final BeanManager beanManager;
	private CreationalContext<MvvmfxCdiApplication> ctx;
	private InjectionTarget<MvvmfxCdiApplication> injectionTarget;
	private final Weld weld;

	public MvvmfxCdiApplication(){
		weld = new Weld();
		WeldContainer weldContainer = weld.initialize();

		MvvmFX.setCustomDependencyInjector((type) -> weldContainer.instance().select(type).get());
	
		MvvmfxProducer mvvmfxProducer = weldContainer.instance().select(MvvmfxProducer.class).get();
		mvvmfxProducer.setHostServices(getHostServices());

		beanManager = weldContainer.getBeanManager();
		
	}

	/**
	 * This method is called when the javafx application is initialized. See {@link javafx.application.Application#init()}
	 * for more details. 
	 *
	 * Unlike the original init method in {@link javafx.application.Application} this method
	 * contains logic to initialize the CDI container. Therefor it's important to
	 * call <code>super.init()</code> when you override this method.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override 
	public void init() throws Exception {
		ctx = beanManager.createCreationalContext(null);
		injectionTarget = beanManager.createInjectionTarget(
				beanManager.createAnnotatedType((Class<MvvmfxCdiApplication>) this.getClass()));
	
		injectionTarget.inject(this, ctx);
		injectionTarget.postConstruct(this);
	}


	/**
	 * This method is called when the application should stop. See {@link javafx.application.Application#stop()}
	 * for more details. 
	 * 
	 * Unlike the original stop method in {@link javafx.application.Application} this method
	 * contains logic to release resources managed by the CDI container. Therefor it's important to
	 * call <code>super.stop()</code> when you override this method.
	 * 
	 * @throws Exception
	 */
	@Override 
	public void stop() throws Exception {
		
		injectionTarget.preDestroy(this);
		injectionTarget.dispose(this);
		
		ctx.release();
		
		weld.shutdown();
	}
}