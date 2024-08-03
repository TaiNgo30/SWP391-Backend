package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.CustomerRequest;
import com.wha.warehousemanagement.dtos.responses.CustomerResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CustomerMapper;
import com.wha.warehousemanagement.models.Customer;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public ResponseObject<?> addProvider(CustomerRequest customerRequest) {
        if (customerRequest.getName() == null || customerRequest.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.PROVIDER_NAME_BLANK);
        } else if (customerRepository.findByName(customerRequest.getName()).isPresent()) {
            throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
        }
        try {
            Customer customer = new Customer();
            customer.setName(customerRequest.getName());
            customer.setEmail(customerRequest.getEmail());
            customer.setPhone(customerRequest.getPhone());
            customer.setAddress(customerRequest.getAddress());
            customerRepository.save(customer);
            Optional<CustomerResponse> response = getLastCustomerResponse();
            if (response.isEmpty()) {
                throw new CustomException(ErrorCode.PROVIDER_ADD_FAILED);
            }
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Provider added successfully",
                    response.get());
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add provider", null);
        }
    }

    private Optional<CustomerResponse> getLastCustomerResponse() {
        List<CustomerResponse> responses = customerRepository.getAllCustomerResponses();
        if (!responses.isEmpty()) {
            return Optional.of(responses.get(0));
        } else {
            return Optional.empty();
        }
    }

    public ResponseObject<?> getAllCustomers() {
        try {
            List<CustomerResponse> list = customerRepository.findAll()
                    .stream()
                    .map(customerMapper::toDto)
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.PROVIDER_NOT_FOUND);
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all providers successfully", list);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all providers", null);
        }
    }

    public ResponseObject<?> getCustomerById(int id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            CustomerResponse response = customerMapper.toDto(customer);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get provider by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get provider by id", null);
        }
    }

    public ResponseObject<?> updateCustomer(int id, CustomerRequest request) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
//            if (request.getName() != null && !request.getName().trim().isEmpty()) {
//                if (customerRepository.existsByName(request.getName())) {
//                    throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
//                }
                customer.setName(request.getName());
//            }
//            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                customer.setEmail(request.getEmail());
//            }
//            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                customer.setPhone(request.getPhone());
//            }
//            if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
                customer.setAddress(request.getAddress());
//            }
            customerRepository.save(customer);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated provider successfully", request);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update provider", null);
        }
    }

    public ResponseObject<?> deleteCustomerById(int id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            customerRepository.delete(customer);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted provider successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete provider", null);
        }
    }

    public ResponseObject<?> deleteAllCustomers() {
        try {
            List<Customer> list = customerRepository.findAll();
            if (!list.isEmpty()) {
                customerRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all providers successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No providers to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete providers", null);
        }
    }
}
