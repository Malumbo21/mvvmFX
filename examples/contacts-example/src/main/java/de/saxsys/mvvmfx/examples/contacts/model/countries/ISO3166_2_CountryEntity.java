package de.saxsys.mvvmfx.examples.contacts.model.countries;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML entity class. These classes represent the structure of the XML files
 * to be loaded.
 */
@XmlRootElement(name = "iso_3166_country")
@XmlAccessorType(XmlAccessType.FIELD) class ISO3166_2_CountryEntity {

	@XmlAttribute(name = "code")
	public String code;

	@XmlElement(name = "iso_3166_subset")
	public List<ISO3166_2_SubsetEntity> subsets;

	@Override
	public String toString() {
		return "CountryEntity " + code;
	}
}
