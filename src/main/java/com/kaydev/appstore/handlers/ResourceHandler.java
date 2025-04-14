package com.kaydev.appstore.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.CategoryMinObj;
import com.kaydev.appstore.models.dto.objects.CategoryObj;
import com.kaydev.appstore.models.dto.objects.CountryObj;
import com.kaydev.appstore.models.dto.objects.DeveloperMinObj;
import com.kaydev.appstore.models.dto.objects.DistributorMinObj;
import com.kaydev.appstore.models.dto.objects.GroupMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerModelMinObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerModelObj;
import com.kaydev.appstore.models.dto.objects.ManufacturerObj;
import com.kaydev.appstore.models.dto.objects.RoleObj;
import com.kaydev.appstore.models.dto.request.is.resource.CreateManufacturerModelRequest;
import com.kaydev.appstore.models.dto.request.is.resource.CreateManufacturerRequest;
import com.kaydev.appstore.models.dto.request.is.resource.EditManufacturerModelRequest;
import com.kaydev.appstore.models.dto.request.is.resource.ManageResourceRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Category;
import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.CrudAction;
import com.kaydev.appstore.models.enums.ListType;
import com.kaydev.appstore.models.enums.ResourceEntity;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.utils.GenericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
public class ResourceHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    public ResponseEntity<BaseResponse> getManufacturers(UserDetailsImpl userDetails, ListType type, String search,
            int page, int size) {
        GenericResponse response = new GenericResponse();
        if (type.equals(ListType.OPTION)) {
            List<ManufacturerMinObj> manufacturers = resourceService.getManufacturerList(StatusType.ACTIVE);

            response.getData().put("manufacturers", manufacturers);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "manufacturerName"));
            Page<ManufacturerObj> manufacturers = resourceService.getManufacturers(pageable, search);

            response.getData().put("manufacturers", manufacturers);
            response.getData().put("currentPageNumber", manufacturers.getNumber());
            response.getData().put("totalPages", manufacturers.getTotalPages());
            response.getData().put("totalItems", manufacturers.getTotalElements());
            response.getData().put("hasNext", manufacturers.hasNext());
        }
        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getRoles(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        List<RoleObj> roles = resourceService.getRolesByUserType(user.getUserType());

        response.getData().put("roles", roles);

        response.setMessage("Roles Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getModels(UserDetailsImpl userDetails, Long manufacturerId, ListType type,
            String search,
            int page, int size) {
        GenericResponse response = new GenericResponse();

        Manufacturer manufacturer = resourceService.getManufacturerRepository().findById(manufacturerId).orElse(null);
        if (manufacturer == null) {
            response.setStatus("error");
            response.setMessage("Manufacturer not found");
            return ResponseEntity.ok(response);
        }
        response.getData().put("manufacturer", new ManufacturerMinObj(manufacturer, true));

        if (type.equals(ListType.OPTION)) {
            List<ManufacturerModelMinObj> models = resourceService.getManufacturerModelList(manufacturerId,
                    StatusType.ACTIVE);

            response.getData().put("models", models);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modelName"));
            Page<ManufacturerModelObj> models = resourceService.getManufacturerModels(pageable, manufacturerId, search);

            // List<ManufacturerModelObj> modelList = models.getContent().stream().map(e ->
            // new ManufacturerModelObj(e))
            // .collect(Collectors.toList());

            response.getData().put("models", models.getContent());
            response.getData().put("currentPageNumber", models.getNumber());
            response.getData().put("totalPages", models.getTotalPages());
            response.getData().put("totalItems", models.getTotalElements());
            response.getData().put("hasNext", models.hasNext());
        }
        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getCategories(UserDetailsImpl userDetails, ListType type, String search,
            int page, int size) {
        GenericResponse response = new GenericResponse();

        if (type.equals(ListType.OPTION)) {
            List<CategoryMinObj> categories = resourceService.getCategoriesMin();
            response.getData().put("categories", categories);
        } else {
            List<CategoryObj> categories = resourceService.getCategories(search);
            response.getData().put("categories", categories);
        }

        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getCountries(UserDetailsImpl userDetails, ListType type, String search,
            int page, int size) {
        GenericResponse response = new GenericResponse();

        if (type.equals(ListType.OPTION)) {
            List<CountryObj> countries = resourceService.getCountries();
            response.getData().put("countries", countries);
        } else {
            List<Country> countries = resourceService.getAllCountries(search);
            response.getData().put("countries", countries);
        }

        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<BaseResponse> getDistributors(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();

        Developer developer = userDetails.getUser().getDeveloper();
        if (developer != null) {
            List<DistributorMinObj> distributors = developerService.getDistributorsByDeveloperId(developer.getId());
            response.getData().put("distributors", distributors);
        } else {
            List<DistributorMinObj> distributors = new ArrayList<>();
            response.getData().put("distributors", distributors);
        }

        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getDevelopers(UserDetailsImpl userDetails) {
        GenericResponse response = new GenericResponse();

        Developer developer = userDetails.getUser().getDeveloper();
        List<DeveloperMinObj> developers = new ArrayList<>();
        if (developer == null) {
            developers = developerService.getDeveloperList();
        } else {
            developers = List.of(new DeveloperMinObj(developer));
        }

        response.getData().put("developers", developers);
        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<BaseResponse> getGroups(UserDetailsImpl userDetails, Long distributorId) {
        GenericResponse response = new GenericResponse();

        Developer developer = userDetails.getUser().getDeveloper();
        if (developer != null) {
            List<GroupMinObj> groups = developerService.getGroupsByDeveloperIdAndDistributorId(developer.getId(),
                    distributorId);
            response.getData().put("groups", groups);
        }

        response.setMessage("List Retrieved Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> manageResource(UserDetailsImpl userDetails,
            ManageResourceRequest<Object> request) {
        GenericResponse response = new GenericResponse();

        if (request.getAction().equals(CrudAction.CREATE)) {
            return createResource(userDetails, request);
        } else if (request.getAction().equals(CrudAction.EDIT)) {
            if (request.getResourceId() != null) {
                return editResource(userDetails, request);
            }
            response.setStatus("error");
            response.setMessage("Resource ID is required");
            return ResponseEntity.ok(response);
        } else if (request.getAction().equals(CrudAction.DELETE)) {
            if (request.getResourceId() != null) {
                return deleteResource(userDetails, request);
            }
            response.setStatus("error");
            response.setMessage("Resource ID is required");
            return ResponseEntity.ok(response);
        } else if (Arrays.asList(CrudAction.ENABLE, CrudAction.DISABLE).contains(request.getAction())) {
            if (request.getResourceId() != null) {
                return resourceStatus(userDetails, request);
            }
            response.setStatus("error");
            response.setMessage("Resource ID is required");
            return ResponseEntity.ok(response);
        }
        response.setStatus("error");
        response.setMessage("Action not found");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<BaseResponse> resourceStatus(UserDetailsImpl userDetails,
            ManageResourceRequest<Object> request) {
        GenericResponse response = new GenericResponse();
        switch (request.getEntity()) {
            case ResourceEntity.MANUFACTURER:
                return editManufacturer(userDetails, request.getAction().name().toLowerCase(), null,
                        request.getResourceId());
            case ResourceEntity.MANUFACTURER_MODEL:
                return editManufacturerModel(userDetails, request.getAction().name()
                        .toLowerCase(), null,
                        request.getResourceId());
            case ResourceEntity.CATEGORY:
                return editCategory(userDetails,
                        request.getAction().name().toLowerCase(), null, request.getResourceId());
            default:
                response.setStatus("error");
                response.setMessage("Entity not found");
                return ResponseEntity.ok(response);
        }

    }

    private ResponseEntity<BaseResponse> createResource(UserDetailsImpl userDetails,
            ManageResourceRequest<Object> request) {
        GenericResponse response = new GenericResponse();
        Object reqObject = request.getData();
        switch (request.getEntity()) {
            case ResourceEntity.MANUFACTURER:
                CreateManufacturerRequest createManufacturerRequest = GenericUtil.convertObjectToClass(reqObject,
                        CreateManufacturerRequest.class);
                return createManufacturer(userDetails, createManufacturerRequest);
            case ResourceEntity.MANUFACTURER_MODEL:
                CreateManufacturerModelRequest createManufacturerModelRequest = GenericUtil.convertObjectToClass(
                        reqObject,
                        CreateManufacturerModelRequest.class);
                return createManufacturerModel(userDetails, createManufacturerModelRequest);
            case ResourceEntity.CATEGORY:
                String value = (String) request.getData();
                return createCategory(userDetails, value);
            default:
                response.setStatus("error");
                response.setMessage("Entity not found");
                return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> editResource(UserDetailsImpl userDetails,
            ManageResourceRequest<Object> request) {
        GenericResponse response = new GenericResponse();
        Object reqObject = request.getData();
        switch (request.getEntity()) {
            case ResourceEntity.MANUFACTURER:
                CreateManufacturerRequest createManufacturerRequest = GenericUtil.convertObjectToClass(
                        reqObject,
                        CreateManufacturerRequest.class);
                return editManufacturer(userDetails, "update", createManufacturerRequest, request.getResourceId());

            case ResourceEntity.MANUFACTURER_MODEL:
                EditManufacturerModelRequest edittManufacturerModelRequest = GenericUtil.convertObjectToClass(
                        reqObject,
                        EditManufacturerModelRequest.class);
                return editManufacturerModel(userDetails, "update", edittManufacturerModelRequest,
                        request.getResourceId());
            case ResourceEntity.CATEGORY:
                String value = (String) request.getData();
                return editCategory(userDetails, "update", value, request.getResourceId());
            default:
                response.setStatus("error");
                response.setMessage("Entity not found");
                return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> deleteResource(UserDetailsImpl userDetails,
            ManageResourceRequest<Object> request) {
        GenericResponse response = new GenericResponse();
        try {

            Long id = request.getResourceId();
            switch (request.getEntity()) {
                case ResourceEntity.MANUFACTURER:
                    resourceService.getManufacturerRepository().deleteById(id);
                    break;
                case ResourceEntity.MANUFACTURER_MODEL:
                    resourceService.getManufacturerModelRespoitory().deleteById(id);
                    break;
                case ResourceEntity.CATEGORY:
                    resourceService.getCategoryRepository().deleteById(id);
                    break;
                default:
                    response.setStatus("error");
                    response.setMessage("Entity not found");
                    return ResponseEntity.ok(response);
            }

            response.setMessage(request.getEntity().name() + " deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Error deleting " + request.getEntity().name() + ". Record already in use");
            return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> createManufacturer(UserDetailsImpl userDetails,
            CreateManufacturerRequest request) {

        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Manufacturer manufacturer = resourceService.getManufacturerByName(request.getManufacturerName());

        if (manufacturer != null) {
            response.setStatus("error");
            response.setMessage("Manufacturer already exists");
            return ResponseEntity.ok(response);
        }

        manufacturer = new Manufacturer();
        manufacturer.setManufacturerName(request.getManufacturerName());
        manufacturer.setUser(user);

        resourceService.getManufacturerRepository().save(manufacturer);

        response.setMessage("Manufacturer created successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<BaseResponse> createManufacturerModel(UserDetailsImpl userDetails,
            CreateManufacturerModelRequest request) {

        GenericResponse response = new GenericResponse();
        try {

            User user = userDetails.getUser();

            Manufacturer manufacturer = resourceService.getManufacturerRepository()
                    .findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

            int inserted = 0;
            for (String model : request.getModels()) {
                try {

                    ManufacturerModel manufacturerModel = resourceService.getManufacturerModelByNameAndManufacturerId(
                            model,
                            request.getManufacturerId());
                    if (manufacturerModel == null) {
                        manufacturerModel = new ManufacturerModel();
                        manufacturerModel.setManufacturer(manufacturer);
                        manufacturerModel.setModelName(model);
                        manufacturerModel.setUser(user);
                        manufacturerModel.setOsType(request.getOsType());
                        resourceService.getManufacturerModelRespoitory().save(manufacturerModel);
                        inserted++;
                    }
                } catch (Exception e) {
                    log.error("Exception while creating manufacturer model:" + model, e);
                    continue;
                }
            }

            response.setMessage("Models created successfully."
                    + (inserted == request.getModels().size() ? "" : " Some models already exist."));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> createCategory(UserDetailsImpl userDetails,
            String name) {

        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();

        Category category = resourceService.getCategoryByName(name);

        if (category != null) {
            response.setStatus("error");
            response.setMessage("Category already exists");
            return ResponseEntity.ok(response);
        }

        category = new Category();
        category.setName(name);
        category.setUser(user);

        resourceService.getCategoryRepository().save(category);

        response.setMessage("Category created successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<BaseResponse> editManufacturer(UserDetailsImpl userDetails, String action,
            CreateManufacturerRequest request, Long id) {

        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();

            Manufacturer manufacturer = resourceService.getManufacturerRepository().findById(id)
                    .orElseThrow(() -> new RuntimeException("Manufacturer with ID not found"));

            if (action.equalsIgnoreCase("update")) {
                manufacturer.setManufacturerName(request.getManufacturerName());
                manufacturer.setUser(user);
            }
            if (action.equalsIgnoreCase("enable")) {
                manufacturer.setStatus(StatusType.ACTIVE);
            }
            if (action.equalsIgnoreCase("disable")) {
                manufacturer.setStatus(StatusType.INACTIVE);
            }
            resourceService.getManufacturerRepository().save(manufacturer);

            response.setMessage("Manufacturer updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> editManufacturerModel(UserDetailsImpl userDetails, String action,
            EditManufacturerModelRequest request, Long id) {

        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();

            ManufacturerModel model = resourceService.getManufacturerModelRespoitory().findById(id)
                    .orElseThrow(() -> new RuntimeException("Model with ID not found"));

            if (action.equalsIgnoreCase("update")) {
                model.setModelName(request.getModel());
                model.setOsType(request.getOsType());
                model.setUser(user);
            }
            if (action.equalsIgnoreCase("enable")) {
                model.setStatus(StatusType.ACTIVE);
            }
            if (action.equalsIgnoreCase("disable")) {
                model.setStatus(StatusType.INACTIVE);
            }

            resourceService.getManufacturerModelRespoitory().save(model);

            response.setMessage("Model updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private ResponseEntity<BaseResponse> editCategory(UserDetailsImpl userDetails, String action, String name,
            Long id) {

        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();

            Category category = resourceService.getCategoryRepository().findById(id)
                    .orElseThrow(() -> new RuntimeException("Category with ID not found"));

            if (action.equalsIgnoreCase("update")) {
                category.setName(name);
                category.setUser(user);
            }
            if (action.equalsIgnoreCase("enable")) {
                category.setStatus(StatusType.ACTIVE);
            }
            if (action.equalsIgnoreCase("disable")) {
                category.setStatus(StatusType.INACTIVE);
            }

            resourceService.getCategoryRepository().save(category);

            response.setMessage("Category updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

}
