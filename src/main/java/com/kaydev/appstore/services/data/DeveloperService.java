package com.kaydev.appstore.services.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.DeveloperMinObj;
import com.kaydev.appstore.models.dto.objects.DeveloperObj;
import com.kaydev.appstore.models.dto.objects.DeveloperSubscriptionObj;
import com.kaydev.appstore.models.dto.objects.DistributorMinObj;
import com.kaydev.appstore.models.dto.objects.DistributorObj;
import com.kaydev.appstore.models.dto.objects.GroupMinObj;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Group;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.SubServiceType;
import com.kaydev.appstore.repository.DeveloperRepository;
import com.kaydev.appstore.repository.DeveloperSettingRepository;
import com.kaydev.appstore.repository.DeveloperSubscriptionRepository;
import com.kaydev.appstore.repository.DistributorRepository;
import com.kaydev.appstore.repository.GroupRepository;
import com.kaydev.appstore.repository.specifications.DistributorSpecification;
import com.kaydev.appstore.repository.specifications.GroupSpecification;

@Service
@Transactional
public class DeveloperService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupSpecification groupSpecification;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private DistributorSpecification distributorSpecification;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private DeveloperSettingRepository developerSettingRepository;

    @Autowired
    private DeveloperSubscriptionRepository developerSubscriptionRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void updateExpiredDevelopers() {
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
        developerRepository.updateExpiredDevelopers(yesterday, StatusType.EXPIRED, StatusType.ACTIVE);
    }

    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public Page<Group> getGroupByFilter(Pageable pageable, Long developerId,
            Long distributorId,
            String search) {
        Specification<Group> spec = groupSpecification.buildSpecification(
                search,
                developerId,
                distributorId);
        return groupRepository.findAll(spec, pageable);
    }

    public Page<GroupMinObj> getGroupByDistributorAndOthers(Pageable pageable, Long developerId,
            Long distributorId,
            String search) {

        return groupRepository.findByDistributorAndOthers(pageable, developerId, distributorId, search);
    }

    public Group getGroupByNameAndDeveloperId(String name, Long developerId) {
        return groupRepository.findByGroupNameAndDeveloperId(name, developerId).orElse(null);
    }

    public List<GroupMinObj> getGroupsByDeveloperIdAndDistributorId(Long developerId, Long distributorId) {
        return groupRepository.findAllByDeveloperIdAndDistributorId(developerId, distributorId);
    }

    public Group getGroupByUuid(String uuid) {
        return groupRepository.findByUuid(uuid).orElse(null);
    }

    public DistributorRepository getDistributorRepository() {
        return distributorRepository;
    }

    public Page<Distributor> getDistributorByFilter(Pageable pageable, Long developerId, StatusType status,
            String search,
            Long countryId) {
        Specification<Distributor> spec = distributorSpecification.buildSpecification(
                developerId,
                search,
                countryId,
                status);
        return distributorRepository.findAll(spec, pageable);
    }

    public Page<DistributorObj> getDistributorByDeveloperFilter(Pageable pageable, Long developerId,
            StatusType status,
            String search) {

        return distributorRepository.findByDeveloperAndOthers(pageable, developerId, status, search);
    }

    public Distributor getDistributorByUuid(String uuid) {
        return distributorRepository.findByUuid(uuid).orElse(null);
    }

    public int countDistributors(Long developerId) {
        return distributorRepository.countByDeveloperId(developerId);
    }

    public Distributor getDistributorByDistributorNameAndDeveloperId(String name, Long developerId) {
        return distributorRepository.findByDistributorNameAndDeveloperId(name, developerId).orElse(null);
    }

    public List<DistributorMinObj> getDistributorsByDeveloperId(Long developerId) {
        return distributorRepository.findAllByDeveloperId(developerId);
    }

    public Distributor getDistributorByIDAndDeveloperId(Long id, Long developerId) {
        return distributorRepository.findByIdAndDeveloperId(id, developerId).orElse(null);
    }

    public DeveloperRepository getDeveloperRepository() {
        return developerRepository;
    }

    public Page<DeveloperObj> getAllDeveloperByFilter(Pageable pageable, String search, Long countryId,
            StatusType status) {
        return developerRepository.findAllByDeveloperAndOthers(pageable, search, countryId, status);
    }

    public Developer getDeveloperByUuid(String uuid) {
        return developerRepository.findByUuid(uuid).orElse(null);
    }

    public List<DeveloperMinObj> getDeveloperList() {
        return developerRepository.findAllList();
    }

    public Developer getDeveloperByName(String name) {
        return developerRepository.findByOrganizationName(name).orElse(null);
    }

    public DeveloperSettingRepository getDeveloperSettingRepository() {
        return developerSettingRepository;
    }

    public DeveloperSubscriptionRepository getDeveloperSubscriptionRepository() {
        return developerSubscriptionRepository;
    }

    public Page<DeveloperSubscriptionObj> getDeveloperSubscriptionByFilter(Pageable pageable, Long developerId,
            SubServiceType serviceType,
            LocalDateTime fromDate, LocalDateTime toDate) {
        return developerSubscriptionRepository.findAllSubscriptions(pageable, developerId, serviceType, fromDate,
                toDate);
    }
}
