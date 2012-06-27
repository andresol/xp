package com.enonic.wem.web.rest2.resource.country;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.country.CountryService;

@Path("misc/country")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class CountryResource
{
    private CountryService countryService;

    @GET
    public CountryResult getAll()
    {
        return new CountryResult( this.countryService.getCountries() );
    }

    @Autowired
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
