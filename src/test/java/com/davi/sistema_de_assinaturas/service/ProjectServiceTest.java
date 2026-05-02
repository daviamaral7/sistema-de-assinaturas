package com.davi.sistema_de_assinaturas.service;

import com.davi.sistema_de_assinaturas.dto.request.ProjectRequestDTO;
import com.davi.sistema_de_assinaturas.dto.response.ProjectResponseDTO;
import com.davi.sistema_de_assinaturas.exceptions.InvalidBusinessRuleException;
import com.davi.sistema_de_assinaturas.mapper.ProjectMapper;
import com.davi.sistema_de_assinaturas.model.Customer;
import com.davi.sistema_de_assinaturas.model.Plan;
import com.davi.sistema_de_assinaturas.model.Project;
import com.davi.sistema_de_assinaturas.model.Subscription;
import com.davi.sistema_de_assinaturas.model.enums.BillingCycle;
import com.davi.sistema_de_assinaturas.model.enums.CustomerStatus;
import com.davi.sistema_de_assinaturas.model.enums.SubscriptionStatus;
import com.davi.sistema_de_assinaturas.repository.CustomerRepository;
import com.davi.sistema_de_assinaturas.repository.ProjectRepository;
import com.davi.sistema_de_assinaturas.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ProjectMapper mapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void givenCustomerWithoutActiveSubscription_whenCreate_thenThrowsInvalidBusinessRuleException() {
        Customer customer = customer(1L);
        ProjectRequestDTO request = new ProjectRequestDTO(customer.getId(), "Billing API");

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));
        when(subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(InvalidBusinessRuleException.class, () -> projectService.create(request));

        verify(projectRepository, never()).save(any(Project.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void givenProjectLimitReached_whenCreate_thenThrowsInvalidBusinessRuleException() {
        Customer customer = customer(1L);
        Subscription subscription = subscription(customer, 2);
        ProjectRequestDTO request = new ProjectRequestDTO(customer.getId(), "Billing API");

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));
        when(subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(projectRepository.countByCustomerIdAndActiveTrue(customer.getId())).thenReturn(2L);

        assertThrows(InvalidBusinessRuleException.class, () -> projectService.create(request));

        verify(projectRepository, never()).save(any(Project.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void givenActiveSubscriptionAndAvailableProjectSlot_whenCreate_thenCreatesProject() {
        Customer customer = customer(1L);
        Subscription subscription = subscription(customer, 2);
        ProjectRequestDTO request = new ProjectRequestDTO(customer.getId(), "Billing API");
        ProjectResponseDTO response = projectResponse(10L, customer, request.name(), "ACTIVE");

        when(customerRepository.findByIdAndStatusNot(customer.getId(), CustomerStatus.DELETED))
                .thenReturn(Optional.of(customer));
        when(subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(projectRepository.countByCustomerId(customer.getId())).thenReturn(1L);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Project.class))).thenReturn(response);

        ProjectResponseDTO result = projectService.create(request);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project savedProject = captor.getValue();

        assertSame(response, result);
        assertEquals(customer, savedProject.getCustomer());
        assertEquals(request.name(), savedProject.getName());
        assertTrue(savedProject.getActive());
    }

    @Test
    void givenProjectLimitReached_whenActivate_thenThrowsInvalidBusinessRuleException() {
        Customer customer = customer(1L);
        Project project = project(10L, customer, false);
        Subscription subscription = subscription(customer, 2);

        when(projectRepository.findByIdAndActiveFalse(project.getId())).thenReturn(Optional.of(project));
        when(subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(projectRepository.countByCustomerIdAndActiveTrue(customer.getId())).thenReturn(2L);

        assertThrows(InvalidBusinessRuleException.class, () -> projectService.activate(project.getId()));

        assertFalse(project.getActive());
        verifyNoInteractions(mapper);
    }

    @Test
    void givenInactiveProjectAndAvailableProjectSlot_whenActivate_thenReactivatesProject() {
        Customer customer = customer(1L);
        Project project = project(10L, customer, false);
        Subscription subscription = subscription(customer, 2);
        ProjectResponseDTO response = projectResponse(project.getId(), customer, project.getName(), "ACTIVE");

        when(projectRepository.findByIdAndActiveFalse(project.getId())).thenReturn(Optional.of(project));
        when(subscriptionRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(projectRepository.countByCustomerIdAndActiveTrue(customer.getId())).thenReturn(1L);
        when(mapper.toResponse(project)).thenReturn(response);

        ProjectResponseDTO result = projectService.activate(project.getId());

        assertSame(response, result);
        assertTrue(project.getActive());
    }

    private static Customer customer(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Acme");
        customer.setStatus(CustomerStatus.ACTIVE);
        return customer;
    }

    private static Subscription subscription(Customer customer, Integer maxProjects) {
        Plan plan = new Plan();
        plan.setId(20L);
        plan.setName("Pro");
        plan.setPrice(BigDecimal.valueOf(99.90));
        plan.setBillingCycle(BillingCycle.MONTHLY);
        plan.setMaxProjects(maxProjects);

        Subscription subscription = new Subscription();
        subscription.setId(30L);
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscription;
    }

    private static Project project(Long id, Customer customer, boolean active) {
        Project project = new Project();
        project.setId(id);
        project.setCustomer(customer);
        project.setName("Billing API");
        project.setActive(active);
        return project;
    }

    private static ProjectResponseDTO projectResponse(Long id, Customer customer, String name, String status) {
        return new ProjectResponseDTO(id, customer.getId(), customer.getName(), name, status, LocalDateTime.now());
    }
}
