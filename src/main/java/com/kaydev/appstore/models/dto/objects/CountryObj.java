package com.kaydev.appstore.models.dto.objects;

import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CountryObj {

    private Long id;
    private String countryName;
    private String countrycode;
    private StatusType status;

    public CountryObj(Country country) {
        this.id = country.getId();
        this.countryName = country.getCountryName();
        this.countrycode = country.getCountryCode();
        this.status = country.getStatus();
    }

}
