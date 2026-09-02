package com.business.basiclogics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.business.entities.HospitalDepartment;
import com.business.entities.InventoryBatch;
import com.business.entities.Product;
import com.business.entities.Requisition;
import com.business.entities.RequisitionItem;
import com.business.repositories.HospitalDepartmentRepository;
import com.business.repositories.InventoryBatchRepository;
import com.business.repositories.ProductRepository;
import com.business.repositories.RequisitionRepository;

@Component
public class HospitalDataInitializer implements CommandLineRunner {

	@Autowired
	private HospitalDepartmentRepository departmentRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryBatchRepository batchRepository;

	@Autowired
	private RequisitionRepository requisitionRepository;

	@Override
	public void run(String... args) throws Exception {
		// 1. Seed Hospital Departments if empty
		if (departmentRepository.count() == 0) {
			List<HospitalDepartment> depts = new ArrayList<>();
			depts.add(new HospitalDepartment("Emergency & Trauma OT", "EMERG-OT", "Dr. Anand Rao", 30, "Ground Floor, Trauma Wing"));
			depts.add(new HospitalDepartment("Intensive Care Unit (ICU)", "ICU-MAIN", "Dr. Priya Deshmukh", 45, "Floor 2, Critical Wing"));
			depts.add(new HospitalDepartment("Central Hospital Pharmacy", "PHARM-CTR", "Chief Pharmacist S. Nair", 0, "Basement 1, Logistics Hub"));
			depts.add(new HospitalDepartment("Cardiology & Cath Lab", "CARDIO-LAB", "Dr. Ramesh Menon", 25, "Floor 3, Cardiac Tower"));
			depts.add(new HospitalDepartment("Orthopedics & Joint Care", "ORTHO-WD", "Dr. Vikram Kulkarni", 40, "Floor 4, West Wing"));
			depts.add(new HospitalDepartment("Oncology & Chemotherapy Unit", "ONCO-CARE", "Dr. Sunita Verma", 35, "Floor 5, Cancer Institute"));

			departmentRepository.saveAll(depts);
			System.out.println("✅ Hospital Departments seeded: 6 departments active.");
		}

		// 2. Seed Medical Formulary Consumables / Drugs if empty
		if (productRepository.count() == 0) {
			List<Product> products = new ArrayList<>();

			Product p1 = new Product();
			p1.setPname("Meropenem 1g IV Infusion");
			p1.setGenericName("Meropenem Trihydrate");
			p1.setCategory("Critical Care Antibiotics");
			p1.setDosageForm("Vial");
			p1.setStorageTemp("2°C - 8°C (Cold Chain)");
			p1.setPprice(850.00);
			p1.setMinSafetyStock(20);
			p1.setReorderPoint(50);
			p1.setLeadTimeDays(2);
			p1.setDailyBurnRate(12.0);
			p1.setPdescription("Ultra broad-spectrum carbapenem antibiotic for severe nosocomial infections, sepsis, and complicated intra-abdominal infections.");
			products.add(p1);

			Product p2 = new Product();
			p2.setPname("Enoxaparin Sodium 40mg/0.4ml");
			p2.setGenericName("Low Molecular Weight Heparin");
			p2.setCategory("Cardiovascular Anticoagulants");
			p2.setDosageForm("Prefilled Syringe");
			p2.setStorageTemp("15°C - 25°C");
			p2.setPprice(420.00);
			p2.setMinSafetyStock(25);
			p2.setReorderPoint(60);
			p2.setLeadTimeDays(3);
			p2.setDailyBurnRate(10.0);
			p2.setPdescription("Antithrombotic prophylactic injection preventing deep vein thrombosis (DVT) and pulmonary embolism post-surgery.");
			products.add(p2);

			Product p3 = new Product();
			p3.setPname("Titanium Femoral Knee Implant System");
			p3.setGenericName("Ti-6Al-4V Orthopedic Knee Prosthesis");
			p3.setCategory("Surgical Implants");
			p3.setDosageForm("Unit Prosthesis");
			p3.setStorageTemp("Ambient Sterile");
			p3.setPprice(45000.00);
			p3.setMinSafetyStock(3);
			p3.setReorderPoint(8);
			p3.setLeadTimeDays(5);
			p3.setDailyBurnRate(1.0);
			p3.setPdescription("High-flexion cruciate-retaining joint replacement implant engineered for total knee arthroplasty (TKA).");
			products.add(p3);

			Product p4 = new Product();
			p4.setPname("Propofol 1% 20ml Emulsion");
			p4.setGenericName("Propofol Injectable Emulsion");
			p4.setCategory("General Anesthesia");
			p4.setDosageForm("Ampoule");
			p4.setStorageTemp("2°C - 8°C (Cold Chain)");
			p4.setPprice(210.00);
			p4.setMinSafetyStock(30);
			p4.setReorderPoint(70);
			p4.setLeadTimeDays(2);
			p4.setDailyBurnRate(16.0);
			p4.setPdescription("Rapid-acting intravenous hypnotic agent for induction and maintenance of general anesthesia and ICU sedation.");
			products.add(p4);

			Product p5 = new Product();
			p5.setPname("Sevoflurane 250ml Inhalation Vapor");
			p5.setGenericName("Sevoflurane Liquid for Inhalation");
			p5.setCategory("Inhalation Anesthesia");
			p5.setDosageForm("Bottle");
			p5.setStorageTemp("15°C - 25°C");
			p5.setPprice(6500.00);
			p5.setMinSafetyStock(5);
			p5.setReorderPoint(12);
			p5.setLeadTimeDays(4);
			p5.setDailyBurnRate(1.5);
			p5.setPdescription("Non-pungent halogenated general inhalation anesthetic agent suitable for adult and pediatric mask inductions.");
			products.add(p5);

			Product p6 = new Product();
			p6.setPname("Sterile Surgical Laparotomy Sponge Pack");
			p6.setGenericName("X-Ray Detectable Cotton Gauze Packs (5s)");
			p6.setCategory("General Surgery Consumables");
			p6.setDosageForm("Sterile Pack");
			p6.setStorageTemp("Ambient Dry");
			p6.setPprice(380.00);
			p6.setMinSafetyStock(40);
			p6.setReorderPoint(90);
			p6.setLeadTimeDays(2);
			p6.setDailyBurnRate(22.0);
			p6.setPdescription("Pre-washed, lint-free, barium sulfate radiopaque sponge packs for fluid absorption during open cavity abdominal surgery.");
			products.add(p6);

			Product p7 = new Product();
			p7.setPname("Human Albumin 20% 100ml Infusion");
			p7.setGenericName("Fractionated Human Normal Albumin");
			p7.setCategory("Critical Care Plasma");
			p7.setDosageForm("Infusion Bottle");
			p7.setStorageTemp("2°C - 8°C (Cold Chain)");
			p7.setPprice(3950.00);
			p7.setMinSafetyStock(8);
			p7.setReorderPoint(20);
			p7.setLeadTimeDays(3);
			p7.setDailyBurnRate(3.0);
			p7.setPdescription("Sterile plasma volume expander for shock resuscitation, severe burn therapy, and acute hypoproteinemia.");
			products.add(p7);

			productRepository.saveAll(products);
			System.out.println("✅ Hospital Medical Formulary seeded: 7 consumables registered.");
		}

		// 3. Seed Realistic Inventory Batches with FEFO Expiry Timelines
		if (batchRepository.count() == 0) {
			List<HospitalDepartment> depts = (List<HospitalDepartment>) departmentRepository.findAll();
			List<Product> products = (List<Product>) productRepository.findAll();

			if (!depts.isEmpty() && !products.isEmpty()) {
				HospitalDepartment emergOt = depts.stream().filter(d -> d.getDeptCode().equals("EMERG-OT")).findFirst().orElse(depts.get(0));
				HospitalDepartment icu = depts.stream().filter(d -> d.getDeptCode().equals("ICU-MAIN")).findFirst().orElse(depts.get(0));
				HospitalDepartment pharm = depts.stream().filter(d -> d.getDeptCode().equals("PHARM-CTR")).findFirst().orElse(depts.get(0));
				HospitalDepartment ortho = depts.stream().filter(d -> d.getDeptCode().equals("ORTHO-WD")).findFirst().orElse(depts.get(0));

				Product mero = products.stream().filter(p -> p.getPname().contains("Meropenem")).findFirst().orElse(products.get(0));
				Product enox = products.stream().filter(p -> p.getPname().contains("Enoxaparin")).findFirst().orElse(products.get(0));
				Product knee = products.stream().filter(p -> p.getPname().contains("Titanium")).findFirst().orElse(products.get(0));
				Product prop = products.stream().filter(p -> p.getPname().contains("Propofol")).findFirst().orElse(products.get(0));
				Product sevo = products.stream().filter(p -> p.getPname().contains("Sevoflurane")).findFirst().orElse(products.get(0));

				List<InventoryBatch> batches = new ArrayList<>();

				// 🚨 CRITICAL EXPIRY BATCH (< 30 days): Meropenem in Central Pharmacy expiring in 18 days!
				batches.add(new InventoryBatch(
					"MERO-2026-B88",
					"010890123400018817260920",
					mero,
					pharm,
					LocalDate.now().minusMonths(11),
					LocalDate.now().plusDays(18), // 18 days left!
					100,
					42,
					850.00
				));

				// 🚨 CRITICAL EXPIRY BATCH (< 30 days): Enoxaparin in Ortho Ward expiring in 24 days!
				batches.add(new InventoryBatch(
					"ENOX-2026-B12",
					"010890123400021217260926",
					enox,
					ortho,
					LocalDate.now().minusMonths(11),
					LocalDate.now().plusDays(24), // 24 days left!
					80,
					35,
					420.00
				));

				// ⚠️ NEAR EXPIRY BATCH (30 - 60 days): Propofol in ICU expiring in 48 days
				batches.add(new InventoryBatch(
					"PROP-2026-B05",
					"010890123400030517261020",
					prop,
					icu,
					LocalDate.now().minusMonths(8),
					LocalDate.now().plusDays(48), // 48 days left
					150,
					65,
					210.00
				));

				// ✅ HEALTHY ACTIVE BATCH (> 120 days): Meropenem in Emergency OT
				batches.add(new InventoryBatch(
					"MERO-2026-B95",
					"010890123400019517270315",
					mero,
					emergOt,
					LocalDate.now().minusMonths(2),
					LocalDate.now().plusDays(210), // healthy
					120,
					110,
					850.00
				));

				// ✅ HEALTHY ACTIVE BATCH: Knee Implants in Ortho
				batches.add(new InventoryBatch(
					"TI-KNEE-2026-A1",
					"010890123400040117280801",
					knee,
					ortho,
					LocalDate.now().minusMonths(4),
					LocalDate.now().plusDays(540), // sterile for 1.5 years
					10,
					7,
					45000.00
				));

				// ✅ HEALTHY ACTIVE BATCH: Sevoflurane in Emergency OT
				batches.add(new InventoryBatch(
					"SEVO-2026-C09",
					"010890123400050917270610",
					sevo,
					emergOt,
					LocalDate.now().minusMonths(3),
					LocalDate.now().plusDays(280),
					15,
					12,
					6500.00
				));

				batchRepository.saveAll(batches);
				System.out.println("✅ Hospital Inventory Batches seeded with FEFO expiry timelines.");

				// 4. Seed an Active Ward Requisition
				Requisition req = new Requisition(
					"REQ-2026-0902",
					emergOt,
					"Staff Nurse Rekha M.",
					"URGENT_24H",
					"PENDING_PHARMACY",
					12750.00,
					"Trauma OT emergency replenishment for weekend scheduled poly-trauma surgeries."
				);
				req.addItem(new RequisitionItem(mero, 15, 850.00));
				requisitionRepository.save(req);
				System.out.println("✅ Hospital Requisitions initialized.");
			}
		}
	}
}
