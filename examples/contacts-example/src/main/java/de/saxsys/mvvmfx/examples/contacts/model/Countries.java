package de.saxsys.mvvmfx.examples.contacts.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "iso_3166_entries")
@XmlAccessorType(XmlAccessType.FIELD)
public class Countries {
    
    @XmlElement(name = "iso_3166_entry")
    private ArrayList<Country> countries;

    public ArrayList<Country> getCountries() {
        return countries;
    }

}
