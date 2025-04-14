package com.kaydev.appstore.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaydev.appstore.models.entities.Country;
import com.kaydev.appstore.models.entities.Permission;
import com.kaydev.appstore.models.entities.Role;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.services.data.ResourceService;
import com.kaydev.appstore.services.data.UserService;

import java.io.IOException;
import java.io.InputStream;

@Service
@Transactional
public class MigrationService {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    public void migrate() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream roleJson = new ClassPathResource("static/roles.json").getInputStream();
            List<Role> roles = objectMapper.readValue(roleJson, new TypeReference<List<Role>>() {
            });

            InputStream permissionJson = new ClassPathResource("static/permissions.json").getInputStream();
            List<Permission> permissions = objectMapper.readValue(permissionJson,
                    new TypeReference<List<Permission>>() {
                    });

            Long isCountrySetup = resourceService.getCountryRepository().count();
            if (isCountrySetup == 0 || null == isCountrySetup) {
                // File countryJson = new ClassPathResource("static/countries.json").getFile();
                InputStream countryJson = new ClassPathResource("static/countries.json").getInputStream();
                List<Country> countries = objectMapper.readValue(countryJson, new TypeReference<List<Country>>() {
                });

                resourceService.getCountryRepository().saveAll(countries);

                List<Permission> savedPermissions = resourceService.getPermissionRepository().saveAll(permissions);

                for (Role r : roles) {

                    if ("ADMIN".equals(r.getName())) {
                        r.getPermissions().addAll(savedPermissions);
                    }

                    else if ("SUPPORT".equals(r.getName())) {
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL, UserType.ADMIN).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    }

                    else if ("DEVELOPER_ADMIN".equals(r.getName())) {
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL, UserType.DEVELOPER).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    } else {
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    }

                    resourceService.getRoleRepository().save(r);
                }

                Role adminRole = resourceService.getRoleRepository().findByName("ADMIN").orElse(null);

                User admin = User.builder()
                        .fullName("Store Admin")
                        .email("kenneth.osekhuemen@iisysgroup.com")
                        .active(true)
                        .verified(true)
                        .password(encoder.encode("K3nn3th111111"))
                        .userType(UserType.ADMIN)
                        .role(adminRole)
                        .build();

                admin.setPermissions(new HashSet<>());
                admin.getPermissions().addAll(adminRole.getPermissions());

                userService.getUserRepository().save(admin);

            }

            List<Permission> existingPermissions = resourceService.getPermissionRepository().findAll();

            if (permissions.size() != existingPermissions.size()) {
                resourceService.getUserPermissionRepository().truncateTable();
                resourceService.getRolePermissionRepository().truncateTable();
                resourceService.getPermissionRepository().truncateTable();

                List<Permission> savedPermissions = resourceService.getPermissionRepository().saveAll(permissions);
                List<Role> savedRoles = resourceService.getRoleRepository().findAll();

                for (Role r : savedRoles) {
                    if ("ADMIN".equals(r.getName())) {
                        r.getPermissions().clear();
                        r.getPermissions().addAll(savedPermissions);
                    }

                    else if ("SUPPORT".equals(r.getName())) {
                        r.getPermissions().clear();
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL, UserType.ADMIN).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    }

                    else if ("DEVELOPER_ADMIN".equals(r.getName())) {
                        r.getPermissions().clear();
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL, UserType.DEVELOPER).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    } else {
                        r.getPermissions()
                                .addAll(savedPermissions.stream().filter(
                                        p -> Arrays.asList(UserType.ALL).contains(p.getRoleType()))
                                        .collect(Collectors.toList()));
                    }

                    resourceService.getRoleRepository().save(r);

                }

                List<User> users = userService.getUserRepository().findAll();
                for (User u : users) {
                    u.setPermissions(new HashSet<>());
                    u.getPermissions().addAll(u.getRole().getPermissions());
                    userService.getUserRepository().save(u);
                }
            }
        } catch (StreamReadException e) {

            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
