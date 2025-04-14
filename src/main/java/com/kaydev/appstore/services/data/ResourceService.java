package com.kaydev.appstore.services.data;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.CategoryMinObj;
import com.kaydev.appstore.models.dto.objects.CategoryObj;
import com.kaydev.appstore.models.dto.objects.CountryObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerModelMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerModelObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerObj;
import com.kaydev.appstore.models.dto.objects.RemoteConnectionObj;
import com.kaydev.appstore.models.dto.objects.RoleObj;
import com.kaydev.appstore.models.dto.objects.dashboard.ManufacturerList;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.RemoteConnection;
import com.kaydev.appstore.models.enums.OsType;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.repository.CategoryRepository;
import com.kaydev.appstore.repository.CountryRepository;
import com.kaydev.appstore.repository.ManufacturerModelRespoitory;
import com.kaydev.appstore.repository.ManufacturerRepository;
import com.kaydev.appstore.repository.PermissionRepository;
import com.kaydev.appstore.repository.RemoteConnectionRepository;
import com.kaydev.appstore.repository.RolePermissionRepository;
import com.kaydev.appstore.repository.RoleRepository;
import com.kaydev.appstore.repository.UserPermissionRepository;

@Service
@Transactional
public class ResourceService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ManufacturerModelRespoitory manufacturerModelRespoitory;

    @Autowired
    private RemoteConnectionRepository remoteConnectionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public List<CategoryObj> getCategories(String search) {
        if (search != null && !search.isEmpty()) {
            return categoryRepository.findBySearch(search);
        }
        return categoryRepository.findAllCategory();
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    public List<CategoryMinObj> getCategoriesMin() {
        return categoryRepository.findAllByStatus(StatusType.ACTIVE);
    }

    public CountryRepository getCountryRepository() {
        return countryRepository;
    }

    public List<Country> getAllCountries(String search) {
        if (search != null && !search.isEmpty()) {
            return countryRepository.findByCountryNameContainingIgnoreCase(search);
        }
        return countryRepository.findAll();
    }

    public List<CountryObj> getCountries() {
        return countryRepository.findAllByStatus(StatusType.ACTIVE);
    }

    public Country getCountryByName(String name) {
        return countryRepository.findByCountryName(name).orElse(null);
    }

    public ManufacturerRepository getManufacturerRepository() {
        return manufacturerRepository;
    }

    public Page<ManufacturerObj> getManufacturers(Pageable pageable, String search) {
        if (search != null && !search.isEmpty()) {
            return manufacturerRepository.findAllBySearch(search, pageable);
        }
        return manufacturerRepository.findAllManufacturer(pageable);
    }

    public List<ManufacturerMinObj> getManufacturerList(StatusType status) {
        return manufacturerRepository.findAllByStatus(status);
    }

    public ManufacturerObj getManufacturerByUuid(String uuid) {
        return manufacturerRepository.findByUuid(uuid).orElse(null);
    }

    public Manufacturer getManufacturerByName(String name) {
        return manufacturerRepository.findByManufacturerName(name).orElse(null);
    }

    public ManufacturerModelRespoitory getManufacturerModelRespoitory() {
        return manufacturerModelRespoitory;
    }

    public Page<ManufacturerModelObj> getManufacturerModels(Pageable pageable, Long manufacturerId, String search) {
        // Specification<ManufacturerModel> spec =
        // manufactuerModelSpecification.buildSpecification(
        // manufacturerId,
        // search);
        return manufacturerModelRespoitory.findAllModelsByManufacturer(pageable, manufacturerId, search);
    }

    public List<ManufacturerModelMinObj> getManufacturerModelList(Long manufacturerId, StatusType status) {
        return manufacturerModelRespoitory.findWithManufacturerId(manufacturerId, status);
    }

    public List<ManufacturerModel> getManufacturerModelsByModelIds(Long manufacturerId, List<Long> modelIds) {
        return manufacturerModelRespoitory.findWithManufacturerAndModelIds(manufacturerId, modelIds);
    }

    public List<ManufacturerModel> getManufacturerModelsByModelIdsAndOsType(Long manufacturerId, List<Long> modelIds,
            OsType osType) {
        return manufacturerModelRespoitory.findWithManufacturerAndModelIdsAndOsType(manufacturerId, modelIds, osType);
    }

    public ManufacturerModelObj getManufacturerModelByUuid(String uuid) {
        return manufacturerModelRespoitory.findByUuid(uuid).orElse(null);
    }

    public ManufacturerModel getManufacturerModelByNameAndManufacturerId(String name, Long manufacturerId) {
        return manufacturerModelRespoitory.findWithModelNameAndManufacturerId(name, manufacturerId).orElse(null);
    }

    public RemoteConnectionRepository getRemoteConnectionRepository() {
        return remoteConnectionRepository;
    }

    public Page<RemoteConnectionObj> getAllRemoteConnectionByFilter(Pageable pageable, Long developerId,
            String connectionId,
            Long terminalId, Long userId, StatusType status) {
        // Specification<RemoteConnection> spec =
        // remoteConnectionSpecification.buildSpecification(
        // developerId,
        // terminalId,
        // userId,
        // connectionId, status);
        return remoteConnectionRepository.findAllByFilter(pageable, developerId, connectionId, terminalId, userId,
                status);
    }

    public RemoteConnection getRemoteConnectionByConnectionId(String connectionId) {
        return remoteConnectionRepository.findByConnectionId(connectionId).orElse(null);
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

    public List<RoleObj> getRolesByUserType(UserType userType) {
        return roleRepository.findAllByUserType(userType);
    }

    public PermissionRepository getPermissionRepository() {
        return permissionRepository;
    }

    public RolePermissionRepository getRolePermissionRepository() {
        return rolePermissionRepository;
    }

    public UserPermissionRepository getUserPermissionRepository() {
        return userPermissionRepository;
    }

    public List<ManufacturerList> getMinManufacturerList() {
        return manufacturerRepository.findMinManufacturerList();
    }
}
