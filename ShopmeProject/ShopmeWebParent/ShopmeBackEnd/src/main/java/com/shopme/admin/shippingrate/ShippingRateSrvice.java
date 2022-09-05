package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateSrvice {
	
	private static final int DIM_DIVISOR = 139; 
	public static final int RATE_PER_PAGE = 10;
	
	@Autowired private ShippingRateRepository rateRepository;
	@Autowired private CountryRepository countryRepository;
	@Autowired private ProductRepository productRepository;
	
	
    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
    	helper.listEntities(pageNum, RATE_PER_PAGE, rateRepository);
    }
    
    public List<Country> listCountry(){
    	return countryRepository.findAllByOrderByNameAsc();
    }
    
    public void save(ShippingRate rateInForm) throws ShippingRateNotFoundException {
    	ShippingRate rateInDb=rateRepository.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
    	
    	boolean foundExistingRateInNEwMode = rateInForm.getId() == null && rateInDb != null;
    	boolean foundDifferentExistingRateEditMode = rateInForm.getId() != null && rateInDb != null;
    	
    	if(foundExistingRateInNEwMode || foundDifferentExistingRateEditMode) {
    		 throw new ShippingRateNotFoundException("this shipping allready existe for "+ 
    	                 rateInForm.getCountry().getName() +" , "+rateInForm.getState());
    	}
    	rateRepository.save(rateInForm);
    }
    
    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
    	try {
			return rateRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ShippingRateNotFoundException("could not foud any Shiping with the given id :"+id);
		}
    	
    }
    
    public void updateCODSupport(Integer id, boolean codSupport) throws ShippingRateNotFoundException {
    	Long countById = rateRepository.countById(id);
    	if(countById == null || countById == 0) {
    		throw new ShippingRateNotFoundException("could not foud any Shiping with the given id :"+id);
    	}
    	rateRepository.updateCODSupport(id, codSupport);
    }
    
    public void deleteShippingRate(Integer id) throws ShippingRateNotFoundException {
    	Long countById = rateRepository.countById(id);
    	if(countById == null || countById == 0) {
    		throw new ShippingRateNotFoundException("could not foud any Shiping with the given id :"+id);
    	}
    	
    	rateRepository.deleteById(id);
    }
    
    //calculet du taux de livraison
    public float calculateShippingCoast(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
    	ShippingRate shippingRate = rateRepository.findByCountryAndState(countryId, state);
    	
    	if(shippingRate == null ) {
    		throw new ShippingRateNotFoundException("No shipping rate found for the given destination then you have enter shipping cost manually");
    	}
    	
    	Product product = productRepository.findById(productId).get();
    	
    	float dimweight = (product.getLength() * product.getWeigth() * product.getHeigth()) / DIM_DIVISOR;
    	float finalweight = product.getWeigth() > dimweight ? product.getWeigth() : dimweight;
    	
    	return finalweight * shippingRate.getRate();
    }
	
}
