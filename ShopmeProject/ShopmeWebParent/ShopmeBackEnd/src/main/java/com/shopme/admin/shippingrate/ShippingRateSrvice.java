package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingRateSrvice {
	
	public static final int RATE_PER_PAGE = 10;
	
	@Autowired private ShippingRateRepository rateRepository;
	@Autowired private CountryRepository countryRepository;
	
	
    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
    	helper.listEntities(pageNum, RATE_PER_PAGE, rateRepository);
    }
    
    public List<Country> listCountry(){
    	return countryRepository.findAllByOrderByNameAsc();
    }
    
    public void save(ShippingRate rateInForm) throws ShippingRateAllReadyExistException {
    	ShippingRate rateInDb=rateRepository.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
    	
    	boolean foundExistingRateInNEwMode = rateInForm.getId() == null && rateInDb != null;
    	boolean foundDifferentExistingRateEditMode = rateInForm.getId() != null && rateInDb != null;
    	
    	if(foundExistingRateInNEwMode || foundDifferentExistingRateEditMode) {
    		 throw new ShippingRateAllReadyExistException("this shipping allready existe for "+ 
    	                 rateInForm.getCountry().getName() +" , "+rateInForm.getState());
    	}
    	rateRepository.save(rateInForm);
    }
    
    public ShippingRate get(Integer id) throws ShippingRateAllReadyExistException {
    	try {
			return rateRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ShippingRateAllReadyExistException("could not foud any Shiping with the given id :"+id);
		}
    	
    }
    
    public void updateCODSupport(Integer id, boolean codSupport) throws ShippingRateAllReadyExistException {
    	Long countById = rateRepository.countById(id);
    	if(countById == null || countById == 0) {
    		throw new ShippingRateAllReadyExistException("could not foud any Shiping with the given id :"+id);
    	}
    	rateRepository.updateCODSupport(id, codSupport);
    }
    
    public void deleteShippingRate(Integer id) throws ShippingRateAllReadyExistException {
    	Long countById = rateRepository.countById(id);
    	if(countById == null || countById == 0) {
    		throw new ShippingRateAllReadyExistException("could not foud any Shiping with the given id :"+id);
    	}
    	
    	rateRepository.deleteById(id);
    }
	
}
