/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import static com.sisllc.instaiml.data.DataGeneratorBase.rand;
import com.sisllc.instaiml.model.ClaimsData;
import com.sisllc.instaiml.model.CoverageDetail;
import com.sisllc.instaiml.model.DrugInventory;
import com.sisllc.instaiml.model.GeographicPricing;
import com.sisllc.instaiml.model.InsuranceCompany;
import com.sisllc.instaiml.model.InsurancePlan;
import com.sisllc.instaiml.model.InsuranceProvider;
import com.sisllc.instaiml.model.Medication;
import com.sisllc.instaiml.model.Member;
import com.sisllc.instaiml.model.Patient;
import com.sisllc.instaiml.model.PatientMember;
import com.sisllc.instaiml.model.Pharmacy;
import com.sisllc.instaiml.model.Physician;
import com.sisllc.instaiml.model.PlanPricing;
import com.sisllc.instaiml.model.Prescription;
import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.repository.ClaimsDataRepository;
import com.sisllc.instaiml.repository.CoverageDetailRepository;
import com.sisllc.instaiml.repository.DrugInventoryRepository;
import com.sisllc.instaiml.repository.GeographicPricingRepository;
import com.sisllc.instaiml.repository.InsuranceCompanyRepository;
import com.sisllc.instaiml.repository.InsurancePlanRepository;
import com.sisllc.instaiml.repository.InsuranceProviderRepository;
import com.sisllc.instaiml.repository.MedicationRepository;
import com.sisllc.instaiml.repository.MemberRepository;
import com.sisllc.instaiml.repository.PatientMemberRepository;
import com.sisllc.instaiml.repository.PatientRepository;
import com.sisllc.instaiml.repository.PharmacyRepository;
import com.sisllc.instaiml.repository.PhysicianRepository;
import com.sisllc.instaiml.repository.PlanPricingRepository;
import com.sisllc.instaiml.repository.PrescriptionRepository;
import com.sisllc.instaiml.repository.UserRepository;
import io.r2dbc.spi.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataGeneratorService extends DataGeneratorBase {

    private static final List<String> unames = List.of("MyAdmin1@1", "MyAdmin1@2", "MyAdmin1@3", "MyAdmin1@4", "MyAdmin1@5");

    private final ConnectionFactory connFactory;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final PhysicianRepository physicianRepository;
    private final DrugInventoryRepository drugInventoryRepository;
    private final PrescriptionRepository prescriptionRepository;

    List<User> users = new ArrayList<>();

    List<Pharmacy> pharmacies = new ArrayList<>();
    List<Medication> medications = new ArrayList<>();
    List<Patient> patients = new ArrayList<>();
    List<Physician> physicians = new ArrayList<>();
    List<DrugInventory> drugInventories = new ArrayList<>();
    List<Prescription> prescriptions = new ArrayList<>();

    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final InsuranceProviderRepository insuranceProviderRepository;
    private final MemberRepository memberRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final InsurancePlanRepository insurancePlanRepository;
    private final PlanPricingRepository planPricingRepository;
    private final GeographicPricingRepository geographicPricingRepository;
    private final CoverageDetailRepository coverageDetailRepository;
    private final ClaimsDataRepository claimsDataRepository;

    List<InsuranceCompany> insuranceCompanies = new ArrayList<>();
    List<InsuranceProvider> insuranceProviders = new ArrayList<>();
    List<Member> members = new ArrayList<>();
    List<PatientMember> patientMembers = new ArrayList<>();
    List<InsurancePlan> insurancePlans = new ArrayList<>();
    List<PlanPricing> planPricings = new ArrayList<>();
    List<GeographicPricing> geographicPricings = new ArrayList<>();
    List<CoverageDetail> coverageDetails = new ArrayList<>();
    List<ClaimsData> claimsData = new ArrayList<>();

    private DatabaseClient dbClient;
    private final R2dbcEntityTemplate template;
    private final TransactionalOperator tx;

    public void seedDataUserOnly() {
        log.debug("seedDataUserOnly users count {} ...", this.users.size());
        dbClient = DatabaseClient.create(connFactory);
        generateUsers()
            .doOnComplete(() -> log.debug("generateUsers complete"))
            .doOnError(e -> log.error("generateUsers error", e))
            .subscribe();
        log.debug("Done seedDataUserOnly users count {}", this.users.size());
    }

    public void seedData() {
        log.debug("seedData users count {} ...", this.users.size());
        this.seedDataAll();
        //this.seedDataReactive();
        //this.seedDataByDbClient();
        log.debug("Done seedData prescriptions count {}", this.prescriptions.size());
    }

    public void seedDataAll() {
        log.debug("seedDataAll entered ... ");
        //retrieveAllData();
        retrieveAllDataForDebug();
        log.debug("seedDataAll ... users={}", this.users.size());
        
        generateAllData()
            .doOnSuccess(c -> System.out.println("SUCCESS: generateAllData"))
            .doOnError(e -> System.err.println("Error generateAllData " + e.getMessage()))
            .subscribe();
        log.debug("Done seedDataAll prescriptions count {}", this.prescriptions.size(), this.claimsData.size());
    }
    
    public Mono<Void> generateAllData() {
        log.debug("generateAllData entered ... ");
        return generateUsers()
            .collectList()
            .doOnNext(list -> this.users = list)
            .then(generatePharmacies().collectList()         
                .doOnNext(list -> this.pharmacies = list)
                .doOnError(e -> log.error("generatePharmacies error", e))
            )
            .then(generateMedications().collectList()
                .doOnNext(list -> this.medications = list)
                .doOnError(e -> log.error("generateMedications error", e))
            )
            .then(generatePatients().collectList()
                .doOnNext(list -> this.patients = list)
                .doOnError(e -> log.error("generatePatients error", e))
            )
            .then(generatePhysicians().collectList()
                .doOnNext(list -> this.physicians = list)
                .doOnError(e -> log.error("generatePhysicians error", e))
            )
            .then(generateDrugInventory().collectList()
                .doOnNext(list -> this.drugInventories = list)
                .doOnError(e -> log.error("generateDrugInventory error", e))
            )
            .then(generateInsuranceCompanies().collectList()
                .doOnNext(list -> this.insuranceCompanies = list)
                .doOnError(e -> log.error("generateInsuranceCompanies error", e))
            )
            .then(generateInsuranceProviders().collectList()
                .doOnNext(list -> this.insuranceProviders = list)
                .doOnError(e -> log.error("generateInsuranceProviders error", e))
            )
            .then(generateInsurancePlans().collectList()
                .doOnNext(list -> this.insurancePlans = list)
                .doOnError(e -> log.error("generateInsuranceProviders error", e))
            )
            .then(generateMembers().collectList()
                .doOnNext(list -> this.members = list)
                .doOnError(e -> log.error("generateMembers error", e))
            )
            .then(generatePatientMembers().collectList()
                .doOnNext(list -> this.patientMembers = list)
                .doOnError(e -> log.error("generatePatientMembers error", e))
            )
            .then(generatePlanPricings().collectList()
                .doOnNext(list -> this.planPricings = list)
                .doOnError(e -> log.error("generatePlanPricings error", e))
            )
            .then(generateGeographicPricings().collectList()
                .doOnNext(list -> this.geographicPricings = list)
                .doOnError(e -> log.error("generateGeographicPricings error", e))
            )
            .then(generateCoverageDetails().collectList()
                .doOnNext(list -> this.coverageDetails = list)
                .doOnError(e -> log.error("generateCoverageDetails error", e))
            )
            .then(generateClaimsData().collectList()
                .doOnNext(list -> this.claimsData = list)
                .doOnError(e -> log.error("generateClaimsData error", e))
            )
            .then(generatePrescriptions().collectList()
                .doOnNext(list -> this.prescriptions = list)
                .doOnError(e -> log.error("generatePrescriptions error", e))
                .then()) // <- Only runs after all lists populated
            .doOnSuccess(v -> log.debug("All data generation complete prescriptions {} claimsData {}", this.prescriptions.size(), this.claimsData.size()));
    }   

    private <T> T getRandom(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalStateException("Required list is empty");
        }
        return list.get(rand.nextInt(list.size()));
    }

    public void seedDataReactive() {
        log.debug("seedDataReactive ...");
        retrieveAllData();

        log.debug("seedData users={}", users.size());
        generateUsers()
            .doOnComplete(() -> log.debug("generateUsers complete"))
            .doOnError(e -> log.error("generateUsers error", e))
            .subscribe();
        log.debug("Done seedData users={}", users.size());

        generatePharmacies()
            .doOnComplete(() -> log.debug("generatePharmacies complete"))
            .doOnError(e -> log.error("generatePharmacies error", e))
            .subscribe();

        generateMedications()
            .doOnComplete(() -> log.debug("generateMedications complete"))
            .doOnError(e -> log.error("generateMedications error", e))
            .subscribe();

        generatePatients()
            .doOnComplete(() -> log.debug("generatePatients complete"))
            .doOnError(e -> log.error("generatePatients error", e))
            .subscribe();

        generatePhysicians()
            .doOnComplete(() -> log.debug("generatePhysicians complete"))
            .doOnError(e -> log.error("generatePhysicians error", e))
            .subscribe();

        generateDrugInventory()
            .doOnComplete(() -> log.debug("generateDrugInventory complete"))
            .doOnError(e -> log.error("generateDrugInventory error", e))
            .subscribe();

        generatePrescriptions()
            .doOnComplete(() -> log.debug("generatePrescriptions complete"))
            .doOnError(e -> log.error("generatePrescriptions error", e))
            .subscribe();

        log.debug("seedData insuranceCompanies={}", insuranceCompanies.size());
        generateInsuranceCompanies()
            .doOnComplete(() -> log.debug("generateInsuranceCompanies complete"))
            .doOnError(e -> log.error("generateInsuranceCompanies error", e))
            .subscribe();

        generateInsuranceProviders()
            .doOnComplete(() -> log.debug("generateInsuranceProviders complete"))
            .doOnError(e -> log.error("generateInsuranceProviders error", e))
            .subscribe();

        generateInsurancePlans()
            .doOnComplete(() -> log.debug("generateUsers complete"))
            .doOnError(e -> log.error("generateUsers error", e))
            .subscribe();

        generateMembers()
            .doOnComplete(() -> log.debug("generateMembers complete"))
            .doOnError(e -> log.error("generateMembers error", e))
            .subscribe();

        generatePatientMembers()
            .doOnComplete(() -> log.debug("generatePatientMembers complete"))
            .doOnError(e -> log.error("generatePatientMembers error", e))
            .subscribe();

        generatePlanPricings()
            .doOnComplete(() -> log.debug("generatePlanPricings complete"))
            .doOnError(e -> log.error("generatePlanPricings error", e))
            .subscribe();

        generateGeographicPricings()
            .doOnComplete(() -> log.debug("generateGeographicPricings complete"))
            .doOnError(e -> log.error("generateGeographicPricings error", e))
            .subscribe();

        generateCoverageDetails()
            .doOnComplete(() -> log.debug("generateCoverageDetails complete"))
            .doOnError(e -> log.error("generateCoverageDetails error", e))
            .subscribe();

        generateClaimsData()
            .doOnComplete(() -> log.debug("generateClaimsData complete"))
            .doOnError(e -> log.error("generateClaimsData error", e))
            .subscribe();

        log.debug("Done seedData patients count={}", patients.size());
    }

    public void retrieveAllData() {
        this.userRepository.findAll().collectList()
            .subscribe(c -> this.users = c);

        this.pharmacyRepository.findAll().collectList()
            .subscribe(c -> this.pharmacies = c);
        this.medicationRepository.findAll().collectList()
            .subscribe(c -> this.medications = c);
        this.patientRepository.findAll().collectList()
            .subscribe(c -> this.patients = c);
        this.physicianRepository.findAll().collectList()
            .subscribe(c -> this.physicians = c);
        this.drugInventoryRepository.findAll().collectList()
            .subscribe(c -> this.drugInventories = c);
        this.prescriptionRepository.findAll().collectList()
            .subscribe(c -> this.prescriptions = c);

        this.insuranceCompanyRepository.findAll().collectList()
            .subscribe(c -> this.insuranceCompanies = c);
        this.insuranceProviderRepository.findAll().collectList()
            .subscribe(c -> this.insuranceProviders = c);
        this.memberRepository.findAll().collectList()
            .subscribe(c -> this.members = c);
        this.patientMemberRepository.findAll().collectList()
            .subscribe(c -> this.patientMembers = c);
        this.insurancePlanRepository.findAll().collectList()
            .subscribe(c -> this.insurancePlans = c);
        this.planPricingRepository.findAll().collectList()
            .subscribe(c -> this.planPricings = c);
        this.geographicPricingRepository.findAll().collectList()
            .subscribe(c -> this.geographicPricings = c);
        this.coverageDetailRepository.findAll().collectList()
            .subscribe(c -> this.coverageDetails = c);
        this.claimsDataRepository.findAll().collectList()
            .subscribe(c -> this.claimsData = c);
    }

    public void retrieveAllDataForDebug() {
        this.users = this.userRepository.findAll().collectList()
            .block();

        this.pharmacies = this.pharmacyRepository.findAll().collectList()
            .block();
        this.medications = this.medicationRepository.findAll().collectList()
            .block();
        this.patients = this.patientRepository.findAll().collectList()
            .block();
        this.physicians = this.physicianRepository.findAll().collectList()
            .block();
        this.drugInventories = this.drugInventoryRepository.findAll().collectList()
            .block();
        this.prescriptions = this.prescriptionRepository.findAll().collectList()
            .block();

        this.insuranceCompanies = this.insuranceCompanyRepository.findAll().collectList()
            .block();
        this.insuranceProviders = this.insuranceProviderRepository.findAll().collectList()
            .block();
        this.members = this.memberRepository.findAll().collectList()
            .block();
        this.patientMembers = this.patientMemberRepository.findAll().collectList()
            .block();
        this.insurancePlans = this.insurancePlanRepository.findAll().collectList()
            .block();
        this.planPricings = this.planPricingRepository.findAll().collectList()
            .block();
        this.geographicPricings = this.geographicPricingRepository.findAll().collectList()
            .block();
        this.coverageDetails = this.coverageDetailRepository.findAll().collectList()
            .block();
        this.claimsData = this.claimsDataRepository.findAll().collectList()
            .block();
    }

    private String getUPWD(int i) {
        if (i < unames.size()) {
            return unames.get(i);
        }

        return FAKER.name().username();
    }

    private Flux<User> generateUsers() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> UserGenerator.generate(getUPWD(i), passwordEncoder))
                .flatMap(rec -> template.insert(User.class)
                .using(rec)
                .doOnError(e -> log.error("Error User: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.users = saved;
                    log.debug("User {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Pharmacy> generatePharmacies() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> PharmacyGenerator.generate())
                .flatMap(rec -> template.insert(Pharmacy.class)
                .using(rec)
                .doOnError(e -> log.error("Error Pharmacy: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.pharmacies = saved;
                    log.debug("Pharmacy {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Medication> generateMedications() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> MedicationGenerator.generate())
                .flatMap(rec -> template.insert(Medication.class)
                .using(rec)
                .doOnError(e -> log.error("Error Medication: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.medications = saved;
                    log.debug("Medication {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Patient> generatePatients() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> PatientGenerator.generate())
                .flatMap(rec -> template.insert(Patient.class)
                .using(rec)
                .doOnError(e -> log.error("Error Patient: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.patients = saved;
                    log.debug("Patient {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Physician> generatePhysicians() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> PhysicianGenerator.generate())
                .flatMap(rec -> template.insert(Physician.class)
                .using(rec)
                .doOnError(e -> log.error("Error Physician: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.physicians = saved;
                    log.debug("Physician {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<DrugInventory> generateDrugInventory() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String pharmacyId = getRandom(pharmacies).getId();
                    String medicationId = getRandom(medications).getId();
                    return DrugInventoryGenerator.generate(pharmacyId, medicationId);
                })
                .flatMap(rec -> template.insert(DrugInventory.class)
                .using(rec)
                .doOnError(e -> log.error("Error DrugInventory: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.drugInventories = saved;
                    log.debug("DrugInventory {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Prescription> generatePrescriptions() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String patientId = getRandom(patients).getId();
                    String physicianId = getRandom(physicians).getId();
                    String medicationId = getRandom(medications).getId();
                    String pharmacyId = getRandom(pharmacies).getId();

                    return PrescriptionGenerator.generate(patientId, physicianId, medicationId, pharmacyId);
                })
                .flatMap(rec -> template.insert(Prescription.class)
                .using(rec)
                .doOnError(e -> log.error("Error Prescription: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.prescriptions = saved;
                    log.debug("Prescription {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<InsuranceCompany> generateInsuranceCompanies() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    return InsuranceCompanyGenerator.generate();
                })
                .flatMap(rec -> template.insert(InsuranceCompany.class)
                .using(rec)
                .doOnError(e -> log.error("Error InsuranceCompany: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.insuranceCompanies = saved;
                    log.debug("InsuranceCompany {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    public Flux<InsuranceProvider> generateInsuranceProviders() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    return InsuranceProviderGenerator.generate();
                })
                .flatMap(rec -> template.insert(InsuranceProvider.class)
                .using(rec)
                .doOnError(e -> log.error("Error InsuranceProvider: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.insuranceProviders = saved;
                    log.debug("InsuranceProvider {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<InsurancePlan> generateInsurancePlans() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String insuranceCompanyId = getRandom(insuranceCompanies).getId();
                    return InsurancePlanGenerator.generate(insuranceCompanyId);
                })
                .flatMap(rec -> template.insert(InsurancePlan.class)
                .using(rec)
                .doOnError(e -> log.error("Error InsurancePlan: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.insurancePlans = saved;
                    log.debug("InsurancePlan {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<Member> generateMembers() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String insPlanId = getRandom(insurancePlans).getId();
                    return MemberGenerator.generate(insPlanId);
                })
                .flatMap(rec -> template.insert(Member.class)
                .using(rec)
                .doOnError(e -> log.error("Error Member: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.members = saved;
                    log.debug("Member {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<PatientMember> generatePatientMembers() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String patientId = getRandom(patients).getId();
                    String memberId = getRandom(members).getId();
                    return PatientMemberGenerator.generate(patientId, memberId);
                })
                .flatMap(rec -> template.insert(PatientMember.class)
                .using(rec)
                .doOnError(e -> log.error("Error PatientMember: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.patientMembers = saved;
                    log.debug("PatientMember {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<PlanPricing> generatePlanPricings() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String randomPlanId = getRandom(insurancePlans).getId();
                    return PlanPricingGenerator.generate(randomPlanId);
                })
                .flatMap(rec -> template.insert(PlanPricing.class)
                .using(rec)
                .doOnError(e -> log.error("Error PlanPricing: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.planPricings = saved;
                    log.debug("PlanPricing {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<GeographicPricing> generateGeographicPricings() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String randomPlanId = getRandom(insurancePlans).getId();
                    return GeographicPricingGenerator.generate(randomPlanId);
                })
                .flatMap(rec -> template.insert(GeographicPricing.class)
                .using(rec)
                .doOnError(e -> log.error("Error GeographicPricing: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.geographicPricings = saved;
                    log.debug("GeographicPricing {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<CoverageDetail> generateCoverageDetails() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String randomPlanId = getRandom(insurancePlans).getId();
                    return CoverageDetailGenerator.generate(randomPlanId);
                })
                .flatMap(rec -> template.insert(CoverageDetail.class)
                .using(rec)
                .doOnError(e -> log.error("Error CoverageDetail: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.coverageDetails = saved;
                    log.debug("CoverageDetail {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    private Flux<ClaimsData> generateClaimsData() {
        return tx.transactional(
            Flux.range(0, 50)
                .map(i -> {
                    String memberId = getRandom(members).getId();
                    String insuranceProviderId = getRandom(insuranceProviders).getId();
                    String insurancePlanId = getRandom(insurancePlans).getId();
                    return ClaimsDataGenerator.generate(memberId, insuranceProviderId, insurancePlanId);
                })
                .flatMap(rec -> template.insert(ClaimsData.class)
                .using(rec)
                .doOnError(e -> log.error("Error ClaimsData: {}", e.getMessage()))
                .thenReturn(rec)
                )
                .collectList()
                .doOnSuccess(saved -> {
                    this.claimsData = saved;
                    log.debug("ClaimsData {}", saved.size());
                })
                .flatMapMany(Flux::fromIterable)
        );
    }

    public void seedDataByDbClient() {
        dbClient = DatabaseClient.create(connFactory);
        this.retrieveAllDataForDebug();

        log.debug("seedDataByDbClient users {}", this.users.size());
        generateUsersByDbClient();
        generatePharmaciesByDbClient();
        generateMedicationsByDbClient();
        generatePatientsByDbClient();
        generatePhysiciansByDbClient();
        generateDrugInventoryByDbClient();
        generatePrescriptionsByDbClient();
        generateInsuranceCompaniesByDbClient();
        generateInsuranceProvidersByDbClient();
        generateInsurancePlansByDbClient();
        generateMembersByDbClient();
        generatePatientMembersByDbClient();
        generatePlanPricingsByDbClient();
        generateGeographicPricingsByDbClient();
        generateCoverageDetailsByDbClient();
        generateClaimsDataByDbClient();
        log.debug("Done seedDataByDbClient users {} claimsData {}", this.users.size(), this.claimsData.size());
    }

    private void generateUsersByDbClient() {
        users = IntStream.range(0, 50)
            .mapToObj(i
                -> UserGenerator.generate(dbClient, getUPWD(i), passwordEncoder)
            )
            .collect(Collectors.toList());
        log.debug("generateUsers DONE {}", users.size());
    }

    private void generatePharmaciesByDbClient() {
        pharmacies = IntStream.range(0, 50)
            .mapToObj(i -> PharmacyGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generateMedicationsByDbClient() {
        medications = IntStream.range(0, 50)
            .mapToObj(i -> MedicationGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generatePatientsByDbClient() {
        patients = IntStream.range(0, 50)
            .mapToObj(i -> PatientGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generatePhysiciansByDbClient() {
        physicians = IntStream.range(0, 50)
            .mapToObj(i -> PhysicianGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generateDrugInventoryByDbClient() {
        drugInventories = IntStream.range(0, 50)
            .mapToObj(i -> DrugInventoryGenerator.generate(dbClient,
            getRandom(pharmacies).getId(),
            getRandom(medications).getId())
            )
            .collect(Collectors.toList());
    }

    private void generatePrescriptionsByDbClient() {
        prescriptions = IntStream.range(0, 50)
            .mapToObj(i -> PrescriptionGenerator.generate(dbClient,
            getRandom(patients).getId(), getRandom(physicians).getId(),
            getRandom(medications).getId(), getRandom(pharmacies).getId())
            )
            .collect(Collectors.toList());
    }

    private void generateInsuranceCompaniesByDbClient() {
        insuranceCompanies = IntStream.range(0, 50)
            .mapToObj(i -> InsuranceCompanyGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generateInsuranceProvidersByDbClient() {
        insuranceProviders = IntStream.range(0, 50)
            .mapToObj(i -> InsuranceProviderGenerator.generate(dbClient))
            .collect(Collectors.toList());
    }

    private void generateInsurancePlansByDbClient() {
        insurancePlans = IntStream.range(0, 50)
            .mapToObj(i -> InsurancePlanGenerator.generate(dbClient,
            getRandom(insuranceCompanies).getId())
            )
            .collect(Collectors.toList());
    }

    private void generateMembersByDbClient() {
        members = IntStream.range(0, 50)
            .mapToObj(i -> MemberGenerator.generate(dbClient,
            getRandom(insurancePlans).getId())
            )
            .collect(Collectors.toList());
    }

    private void generatePatientMembersByDbClient() {
        this.patientMembers = IntStream.range(0, 50)
            .mapToObj(i -> PatientMemberGenerator.generate(dbClient,
            getRandom(patients).getId(), getRandom(members).getId())
            )
            .collect(Collectors.toList());
    }

    private void generatePlanPricingsByDbClient() {
        this.planPricings = IntStream.range(0, 50)
            .mapToObj(i -> PlanPricingGenerator.generate(dbClient,
            insurancePlans.get(rand.nextInt(insurancePlans.size())).getId())
            )
            .collect(Collectors.toList());
    }

    private void generateGeographicPricingsByDbClient() {
        this.geographicPricings = IntStream.range(0, 50)
            .mapToObj(i -> GeographicPricingGenerator.generate(dbClient,
            getRandom(insurancePlans).getId()))
            .collect(Collectors.toList());
    }

    private void generateCoverageDetailsByDbClient() {
        this.coverageDetails = IntStream.range(0, 50)
            .mapToObj(i -> CoverageDetailGenerator.generate(dbClient,
            getRandom(insurancePlans).getId())
            )
            .collect(Collectors.toList());
    }

    private void generateClaimsDataByDbClient() {
        this.claimsData = IntStream.range(0, 50)
            .mapToObj(i -> ClaimsDataGenerator.generate(dbClient,
            getRandom(members).getId(), getRandom(insuranceProviders).getId(), getRandom(insurancePlans).getId())
            )
            .collect(Collectors.toList());
    }
}
