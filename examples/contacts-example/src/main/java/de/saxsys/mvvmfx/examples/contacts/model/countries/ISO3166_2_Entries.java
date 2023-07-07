package de.saxsys.mvvmfx.examples.contacts.model.countries;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "iso_3166_2_entries")
@XmlAccessorType(XmlAccessType.FIELD)
public class ISO3166_2_Entries {

    @XmlElement(name = "iso_3166_country")
    public ArrayList<ISO3166_2_CountryEntity> countryEntities;
    
}
