package com.kaydev.appstore.handlers;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kaydev.appstore.models.dto.objects.DeveloperObj;
import com.kaydev.appstore.models.dto.objects.DeveloperSubscriptionObj;
import com.kaydev.appstore.models.dto.objects.DistributorObj;
import com.kaydev.appstore.models.dto.objects.GroupMinObj;
import com.kaydev.appstore.models.dto.objects.GroupObj;
import com.kaydev.appstore.models.dto.objects.export.TerminalChargableObjExp;
import com.kaydev.appstore.models.dto.request.is.AddSubscriptionRequest;
import com.kaydev.appstore.models.dto.request.is.CreateDeveloperRequest;
import com.kaydev.appstore.models.dto.request.is.CreateDistributorRequest;
import com.kaydev.appstore.models.dto.request.is.CreateGroup;
import com.kaydev.appstore.models.dto.request.is.EditDeveloperRequest;
import com.kaydev.appstore.models.dto.request.is.EditDistributorRequest;
import com.kaydev.appstore.models.dto.request.is.SearchParams;
import com.kaydev.appstore.models.dto.request.is.UpdateDeveloperSettingRequest;
import com.kaydev.appstore.models.dto.request.is.UpdateDeveloperStatusRequest;
import com.kaydev.appstore.models.dto.response.BaseResponse;
import com.kaydev.appstore.models.dto.response.GenericResponse;
import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.DeveloperSetting;
import com.kaydev.appstore.models.entities.DeveloperSubscription;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.SubServiceType;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.security.implementation.UserDetailsImpl;
import com.kaydev.appstore.services.NotificationService;
import com.kaydev.appstore.services.data.DeveloperService;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.services.data.UserService;
import com.kaydev.appstore.utils.ExcelUtil;
import com.kaydev.appstore.utils.GenericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeveloperHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<BaseResponse> getDevelopers(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();

        Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));

        Page<DeveloperObj> developers = developerService.getAllDeveloperByFilter(pageable,
                searchParams.getSearch(),
                searchParams.getCountryId(),
                searchParams.getStatus());

        response.getData().put("developers", developers.getContent());
        response.getData().put("currentPageNumber", developers.getNumber());
        response.getData().put("totalPages", developers.getTotalPages());
        response.getData().put("totalItems", developers.getTotalElements());
        response.getData().put("hasNext", developers.hasNext());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getDeveloper(UserDetailsImpl userDetails, String developerUuid) {

        GenericResponse response = new GenericResponse();

        Developer developer = developerService.getDeveloperByUuid(developerUuid);

        if (developer == null) {
            response.setStatus("error");
            response.setMessage("Developer not found");
            return ResponseEntity.status(404).body(response);
        }

        DeveloperObj developerObj = new DeveloperObj(developer);

        response.setMessage("Developer retrieved successfully");
        response.getData().put("developer", developerObj);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> editDeveloper(UserDetailsImpl userDetails, String developerUuid,
            EditDeveloperRequest request) {
        GenericResponse response = new GenericResponse();

        try {
            Developer developer = developerService.getDeveloperByUuid(developerUuid);
            if (developer == null) {
                response.setStatus("error");
                response.setMessage("Developer not found");
                return ResponseEntity.status(404).body(response);
            }

            if (request.getCountryId() != null) {
                Country country = resourceService.getCountryRepository().findById(request.getCountryId())
                        .orElseThrow(() -> new RuntimeException("Country not found"));

                developer.setCountry(country);
            }
            if (request.getOrganizationName() != null) {
                developer.setOrganizationName(request.getOrganizationName());
            }
            if (request.getSupportEmail() != null) {
                developer.setSupportEmail(request.getSupportEmail());
            }
            if (request.getWebsiteUrl() != null) {
                developer.setWebsiteUrl(request.getWebsiteUrl());
            }

            developerService.getDeveloperRepository().save(developer);

            response.setMessage("Developer updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("editDeveloper", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while editting developer");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> getDeveloperSubscription(UserDetailsImpl userDetails,
            SearchParams searchParams) {
        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Long devId = searchParams.getDeveloperId();
            if (developer != null) {
                devId = developer.getId();
            }

            LocalDateTime from = null;
            if (searchParams.getFrom() != null) {
                from = searchParams.getFrom().atStartOfDay();
            }

            LocalDateTime to = null;
            if (searchParams.getTo() != null) {
                to = searchParams.getTo().atTime(LocalTime.MAX);
            }

            Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id"));

            Page<DeveloperSubscriptionObj> subscriptions = developerService.getDeveloperSubscriptionByFilter(pageable,
                    devId, searchParams.getSubServiceType(), from, to);

            // List<DeveloperSubscriptionObj> subscriptionsObj =
            // subscriptions.getContent().stream()
            // .map(d -> new DeveloperSubscriptionObj(d))
            // .collect(Collectors.toList());

            response.getData().put("subscriptions", subscriptions.getContent());
            response.getData().put("currentPageNumber", subscriptions.getNumber());
            response.getData().put("totalPages", subscriptions.getTotalPages());
            response.getData().put("totalItems", subscriptions.getTotalElements());
            response.getData().put("hasNext", subscriptions.hasNext());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("getDeveloperSubscription", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while getting developer subscriptions");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> updateDeveloperSettings(UserDetailsImpl userDetails, String developerUuid,
            UpdateDeveloperSettingRequest request) {

        GenericResponse response = new GenericResponse();

        try {
            Developer developer = developerService.getDeveloperByUuid(developerUuid);
            if (developer == null) {
                response.setStatus("error");
                response.setMessage("Developer not found");
                return ResponseEntity.status(404).body(response);
            }

            DeveloperSetting setting = developer.getSetting();
            if (setting == null) {
                setting = new DeveloperSetting();
                setting.setDeveloper(developer);
            }

            setting.setCanAddApp(request.isCanAddApp());
            setting.setCanPush(request.isCanPush());
            setting.setCanAddDistributor(request.isCanAddDistributor());
            setting.setCanRemote(request.isCanRemote());

            if (request.getMaxDistributors() > 0) {
                setting.setMaxDistributors(request.getMaxDistributors());
            }
            if (request.getMaxApps() > 0) {
                setting.setMaxApps(request.getMaxApps());
            }

            if (request.getMaxDistributors() > 0) {
                setting.setMaxDistributors(request.getMaxDistributors());
            }

            developerService.getDeveloperSettingRepository().save(setting);
            response.setMessage("Developer settings updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("updateDeveloperSettings", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while updating developer settings");
            return ResponseEntity.ok(response);
        }

    }

    public ResponseEntity<BaseResponse> addDeveloperSubscription(UserDetailsImpl userDetails, String developerUuid,
            AddSubscriptionRequest request) {

        GenericResponse response = new GenericResponse();

        try {
            User user = userDetails.getUser();
            Developer developer = developerService.getDeveloperByUuid(developerUuid);
            if (developer == null) {
                response.setStatus("error");
                response.setMessage("Developer not found");
                return ResponseEntity.status(404).body(response);
            }

            DeveloperSubscription subscription = new DeveloperSubscription();
            subscription.setDeveloper(developer);
            subscription.setServiceType(request.getServiceType());
            subscription.setAmount(request.getAmount());
            subscription.setCurrency(request.getCurrency());
            subscription.setUser(user);

            if (request.getServiceType().equals(SubServiceType.STORE_ACCESS)) {
                subscription.setDuration(request.getDuration() + " Months");
                subscription.setPreviousValue(developer.getExpiryDate().toString());

                if (developer.getStatus().equals(StatusType.EXPIRED)) {
                    developer.setStatus(StatusType.ACTIVE);
                    developer.setExpiryDate(LocalDateTime.now().plusMonths(request.getDuration()));
                } else {
                    developer.setExpiryDate(developer.getExpiryDate().plusMonths(request.getDuration()));
                }
                subscription.setAfterValue(developer.getExpiryDate().toString());
                subscription.setDescription("App store subscription for " + request.getDuration()
                        + " months. | Reference: " + subscription.getReference());
            }

            if (request.getServiceType().equals(SubServiceType.REMOTE_TIME)) {
                subscription.setDuration(request.getDuration() + " Hours");
                subscription.setPreviousValue(developer.getRemoteHours() + " Hours");

                developer.setRemoteHours(developer.getRemoteHours() + request.getDuration());
                subscription.setAfterValue(developer.getRemoteHours() + " Hours");
                subscription.setDescription("Remote time subscription for " + request.getDuration()
                        + " hours. | Reference: " + subscription.getReference());
            }

            developerService.getDeveloperRepository().save(developer);
            developerService.getDeveloperSubscriptionRepository().save(subscription);

            response.setMessage("Subscription added successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("addDeveloperSubscription", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while adding developer subscription");
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> updateDeveloperStatus(UserDetailsImpl userDetails, String developerUuid,
            UpdateDeveloperStatusRequest request) {

        GenericResponse response = new GenericResponse();

        try {
            Developer developer = developerService.getDeveloperByUuid(developerUuid);
            if (developer == null) {
                response.setStatus("error");
                response.setMessage("Developer not found");
                return ResponseEntity.status(404).body(response);
            }

            if (!Arrays.asList(StatusType.ACTIVE, StatusType.INACTIVE, StatusType.EXPIRED)
                    .contains(request.getStatus())) {
                response.setStatus("error");
                response.setMessage("Invalid status");
                return ResponseEntity.ok(response);
            }

            if (developer.getStatus().equals(StatusType.EXPIRED) && request.getStatus().equals(StatusType.ACTIVE)
                    && (request.getExpiryPeriod() <= 0)) {
                response.setStatus("error");
                response.setMessage("Expiry Period is required");
                return ResponseEntity.ok(response);
            }

            if (developer.getStatus().equals(StatusType.EXPIRED) && request.getStatus().equals(StatusType.ACTIVE)) {
                AddSubscriptionRequest addSubscriptionRequest = AddSubscriptionRequest.builder()
                        .duration(Long.valueOf(request.getExpiryPeriod())).serviceType(SubServiceType.STORE_ACCESS)
                        .amount(0.0).currency("USD").build();

                return addDeveloperSubscription(userDetails, developerUuid, addSubscriptionRequest);
            } else {
                developer.setStatus(request.getStatus());
            }

            developerService.getDeveloperRepository().save(developer);

            response.setMessage("Developer status updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("updateDeveloperStatus", e);
            response.setStatus("error");
            response.setMessage("Exception occurred while updating developer status");
            return ResponseEntity.ok(response);
        }
    }

    public ByteArrayOutputStream exportDeveloperChargable(UserDetailsImpl userDetails, String developerUuid,
            SearchParams searchParams) {
        try {
            Developer developer = developerService.getDeveloperByUuid(developerUuid);
            if (developer == null) {
                return null;
            }

            if (searchParams.getFrom() == null || searchParams.getTo() == null) {
                return null;
            }

            int page = 0;
            int size = 1000;
            List<TerminalChargableObjExp> data = new ArrayList<>();
            Page<TerminalChargableObjExp> chargableTerminals;

            do {
                Pageable pageable = PageRequest.of(page, size);

                chargableTerminals = terminalService.getTerminalChargableCount(pageable, developer.getId(),
                        searchParams.getFrom().atStartOfDay(), searchParams.getTo().atTime(LocalTime.MAX));

                data.addAll(chargableTerminals.getContent());
                page++;
            } while (chargableTerminals.hasNext());

            ExcelUtil<TerminalChargableObjExp> excelUtil = new ExcelUtil<>();

            return excelUtil.generateExcel(data, TerminalChargableObjExp.class);

        } catch (Exception e) {
            log.error("exportDeveloperChargable", e);
            return null;
        }
    }

    public ResponseEntity<BaseResponse> getDeveloperDistributors(UserDetailsImpl userDetails,
            SearchParams searchParams) {

        GenericResponse response = new GenericResponse();

        Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));

        Developer developer = userDetails.getUser().getDeveloper();
        Long devId = searchParams.getDeveloperId();
        if (developer != null) {
            devId = developer.getId();
        }

        Page<DistributorObj> distributors = developerService.getDistributorByDeveloperFilter(pageable,
                devId,
                searchParams.getStatus(), searchParams.getSearch());

        response.getData().put("distributors", distributors.getContent());
        response.getData().put("currentPageNumber", distributors.getNumber());
        response.getData().put("totalPages", distributors.getTotalPages());
        response.getData().put("totalItems", distributors.getTotalElements());
        response.getData().put("hasNext", distributors.hasNext());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getDeveloperDistributor(UserDetailsImpl userDetails, String distributorUuid) {

        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Distributor distributor = developerService.getDistributorByUuid(distributorUuid);

        if (distributor == null) {
            response.setStatus("error");
            response.setMessage("Distributor not found");
            return ResponseEntity.status(404).body(response);
        }

        if (developer != null && developer.getId() != distributor.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Distributor not found");
            return ResponseEntity.status(404).body(response);
        }

        response.setMessage("Distributor retrieved successfully");

        response.getData().put("distributor", new DistributorObj(distributor));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> editDeveloperDistributor(UserDetailsImpl userDetails, String distributorUuid,
            EditDistributorRequest request) {

        GenericResponse response = new GenericResponse();
        try {

            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Distributor distributor = developerService.getDistributorByUuid(distributorUuid);
            if (distributor == null) {
                response.setStatus("error");
                response.setMessage("Distributor not found");
                return ResponseEntity.status(404).body(response);
            }

            if (developer != null && developer.getId() != distributor.getDeveloper().getId()) {
                response.setStatus("error");
                response.setMessage("Distributor not found");
                return ResponseEntity.status(404).body(response);
            }

            if (request.getCountryId() != null) {
                if (request.getCountryId() != null) {
                    Country country = resourceService.getCountryRepository().findById(request.getCountryId())
                            .orElseThrow(() -> new RuntimeException("Country not found"));

                    distributor.setCountry(country);
                }
            }
            if (request.getDistributorName() != null) {
                distributor.setDistributorName(request.getDistributorName());
            }
            if (request.getContactName() != null) {
                distributor.setContactName(request.getContactName());
            }
            if (request.getContactEmail() != null) {
                distributor.setContactEmail(request.getContactEmail());
            }

            developerService.getDistributorRepository().save(distributor);
            response.setMessage("Distributor updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> deleteDeveloperDistributor(UserDetailsImpl userDetails,
            String distributorUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Distributor distributor = developerService.getDistributorByUuid(distributorUuid);

        if (distributor == null) {
            response.setStatus("error");
            response.setMessage("Distributor not found");
            return ResponseEntity.status(404).body(response);
        }

        if (developer != null && developer.getId() != distributor.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Distributor not found");
            return ResponseEntity.status(404).body(response);
        }

        int associatedTerminals = terminalService.countTerminalByDistributorId(distributor.getId());
        if (associatedTerminals > 0) {
            response.setStatus("error");
            response.setMessage("Distributor has associated terminals, reassign terminals before deleting");
            return ResponseEntity.status(400).body(response);
        }

        developerService.getDistributorRepository().delete(distributor);

        response.setMessage("Distributor deleted successfully");
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<BaseResponse> getDeveloperGroups(UserDetailsImpl userDetails, SearchParams searchParams) {
        GenericResponse response = new GenericResponse();

        Developer developer = userDetails.getUser().getDeveloper();

        Long devId = searchParams.getDeveloperId();
        if (developer != null) {
            devId = developer.getId();
        }

        Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));

        Page<GroupMinObj> groups = developerService.getGroupByDistributorAndOthers(pageable, devId,
                searchParams.getDistributorId(),
                searchParams.getSearch());

        response.getData().put("groups", groups.getContent());
        response.getData().put("currentPageNumber", groups.getNumber());
        response.getData().put("totalPages", groups.getTotalPages());
        response.getData().put("totalItems", groups.getTotalElements());
        response.getData().put("hasNext", groups.hasNext());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> getDeveloperGroup(UserDetailsImpl userDetails, String groupUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Group group = developerService.getGroupByUuid(groupUuid);

        if (group == null) {
            response.setStatus("error");
            response.setMessage("Group not found");
            return ResponseEntity.status(404).body(response);
        }

        if (developer != null && developer.getId() != group.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Group not found");
            return ResponseEntity.status(404).body(response);
        }

        response.setMessage("Group retrieved successfully");
        response.getData().put("group", new GroupObj(group));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<BaseResponse> deleteDeveloperGroup(UserDetailsImpl userDetails, String groupUuid) {
        GenericResponse response = new GenericResponse();

        User user = userDetails.getUser();
        Developer developer = user.getDeveloper();

        Group group = developerService.getGroupByUuid(groupUuid);

        if (group == null) {
            response.setStatus("error");
            response.setMessage("Group not found");
            return ResponseEntity.status(404).body(response);
        }

        if (developer != null && developer.getId() != group.getDeveloper().getId()) {
            response.setStatus("error");
            response.setMessage("Group not found");
            return ResponseEntity.status(404).body(response);
        }

        int associatedTerminals = terminalService.countTerminalByGroupId(group.getId());
        if (associatedTerminals > 0) {
            response.setStatus("error");
            response.setMessage("Group has associated terminals, reassign terminals before deleting");
            return ResponseEntity.status(400).body(response);
        }

        developerService.getGroupRepository().delete(group);

        response.setMessage("Group deleted successfully");
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<BaseResponse> createDeveloper(UserDetailsImpl userDetails, CreateDeveloperRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            Developer developer = developerService.getDeveloperByName(request.getOrganizationName());

            if (developer != null) {
                response.setStatus("error");
                response.setMessage("Developer account already exists");
                return ResponseEntity.ok(response);
            }

            User existingContact = userService.getUserByEmail(request.getContactEmail());

            if (existingContact != null) {
                response.setStatus("error");
                response.setMessage("Contact email already exists");
                return ResponseEntity.ok(response);
            }

            User user = userDetails.getUser();

            Country country = resourceService.getCountryRepository().findById(request.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Country not found"));

            LocalDateTime expiryDate = LocalDateTime.now().plusMonths(request.getExpiryPeriod());

            developer = new Developer();
            developer.setOrganizationName(request.getOrganizationName());
            developer.setCountry(country);
            developer.setSupportEmail(request.getSupportEmail());
            developer.setWebsiteUrl(request.getWebsiteUrl());
            developer.setUser(user);
            developer.setExpiryDate(expiryDate);

            developerService.getDeveloperRepository().save(developer);

            DeveloperSetting setting = new DeveloperSetting();
            setting.setDeveloper(developer);
            developerService.getDeveloperSettingRepository().save(setting);

            Role role = resourceService.getRoleRepository().findByName("DEVELOPER_ADMIN").orElse(null);

            if (role != null) {
                String password = GenericUtil.generateSecurePassword();

                User developerUser = new User();
                developerUser.setDeveloper(developer);
                developerUser.setEmail(request.getContactEmail());
                developerUser.setFullName(request.getContactPerson());
                developerUser.setUserType(UserType.DEVELOPER);
                developerUser.setPassword(encoder.encode(password));
                developerUser.setRole(role);
                developerUser.setPermissions(new HashSet<>());
                developerUser.getPermissions().addAll(role.getPermissions());

                userService.getUserRepository().save(developerUser);

                notificationService.sendWelcomeEmail(developerUser, password);
            }

            response.setMessage("Developer account created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> createDistibutor(UserDetailsImpl userDetails,
            CreateDistributorRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            if (developer == null) {
                if (request.getDeveloperId() > 0) {
                    developer = developerService.getDeveloperRepository().findById(request.getDeveloperId())
                            .orElseThrow(() -> new RuntimeException("No Developer found"));
                } else {
                    response.setStatus("error");
                    response.setMessage("Developer is required");
                    return ResponseEntity.ok(response);
                }
            }

            if (!developer.getSetting().isCanAddDistributor()) {
                response.setStatus("error");
                response.setMessage("You are not allowed to add distributors");
                return ResponseEntity.ok(response);
            }

            Distributor distributor = developerService
                    .getDistributorByDistributorNameAndDeveloperId(request.getDistributorName(), developer.getId());

            if (distributor != null) {
                response.setStatus("error");
                response.setMessage("Distributor name already exists");
                return ResponseEntity.ok(response);
            }

            int countDistributors = developerService.countDistributors(developer.getId());

            if ((countDistributors + 1) > developer.getSetting().getMaxDistributors()) {
                response.setStatus("error");
                response.setMessage("Maximum number of distributors reached");
                return ResponseEntity.ok(response);
            }

            Country country = resourceService.getCountryRepository().findById(request.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Country not found"));

            distributor = new Distributor();
            distributor.setDistributorName(request.getDistributorName());
            distributor.setCountry(country);
            distributor.setDeveloper(developer);
            distributor.setContactName(request.getContactName());
            distributor.setContactEmail(request.getContactEmail());
            distributor.setUser(user);

            developerService.getDistributorRepository().save(distributor);

            response.setMessage("Distributor account created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<BaseResponse> createGroup(UserDetailsImpl userDetails, CreateGroup request) {
        GenericResponse response = new GenericResponse();
        try {
            User user = userDetails.getUser();
            Developer developer = user.getDeveloper();

            Group exist = developerService.getGroupByNameAndDeveloperId(request.getGroupName(), developer.getId());

            if (exist != null) {
                response.setStatus("error");
                response.setMessage("Group name already exists");
                return ResponseEntity.ok(response);
            }

            Manufacturer manufacturer = resourceService.getManufacturerRepository()
                    .findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

            List<ManufacturerModel> models = resourceService.getManufacturerModelsByModelIdsAndOsType(
                    manufacturer.getId(),
                    request.getModelIds(), request.getOsType());

            Distributor distributor = developerService.getDistributorRepository().findById(request.getDistributorId())
                    .orElseThrow(() -> new RuntimeException("Distributor not found"));

            if (distributor != null && distributor.getDeveloper().getId() != developer.getId()) {
                response.setStatus("error");
                response.setMessage("Distributor not associated with this developer");
                return ResponseEntity.ok(response);
            }

            Group group = new Group();
            group.setGroupName(request.getGroupName());
            group.setManufacturer(manufacturer);
            group.setDistributor(distributor);
            group.setDeveloper(developer);
            group.setModels(models);
            group.setOsType(request.getOsType());
            group.setUser(user);

            developerService.getGroupRepository().save(group);

            response.setMessage("Group created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }

    }
}
