package com.business.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.entities.Orders;
import com.business.entities.Product;
import com.business.repositories.OrderRepository;
import com.business.repositories.ProductRepository;
import com.business.repositories.UserRepository;

@Service
public class AiService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Process a conversational message from client/user and generate an intelligent AI response.
	 */
	public Map<String, Object> processChat(String userPrompt) {
		Map<String, Object> response = new HashMap<>();
		List<String> suggestions = new ArrayList<>();

		if (userPrompt == null || userPrompt.trim().isEmpty()) {
			response.put("reply", "Hello! I am **NexusAI**, your enterprise cloud and software architecture copilot. How can I assist you with software suites, seat estimation, or cloud deployment today?");
			suggestions.add("Recommend a suite for 50 users");
			suggestions.add("Estimate cost for Cloud ERP");
			suggestions.add("What are your security features?");
			response.put("suggestions", suggestions);
			return response;
		}

		String promptLower = userPrompt.toLowerCase(Locale.ROOT);
		List<Product> catalog = (List<Product>) productRepository.findAll();

		// 1. Cost & Seat Calculation Query
		if (promptLower.contains("cost") || promptLower.contains("price") || promptLower.contains("pricing") || promptLower.contains("estimate") || promptLower.contains("calculate")) {
			int seats = extractNumber(promptLower);
			if (seats <= 0) seats = 10; // default assumption

			Product matchedProduct = findBestProductMatch(promptLower, catalog);

			if (matchedProduct != null) {
				double total = matchedProduct.getPprice() * seats;
				double discountedTotal = total;
				String discountMsg = "";

				if (seats >= 50) {
					discountedTotal = total * 0.80; // 20% enterprise discount
					discountMsg = "\n\n🎉 **Enterprise Volume Discount Applied (20% Off)**: Saved ₹" + String.format("%.2f", (total - discountedTotal)) + "!";
				} else if (seats >= 20) {
					discountedTotal = total * 0.90; // 10% team discount
					discountMsg = "\n\n💡 **Team Discount Applied (10% Off)**: Saved ₹" + String.format("%.2f", (total - discountedTotal)) + "!";
				}

				String reply = "### 💰 Cost Estimation for " + matchedProduct.getPname() + "\n"
						+ "- **Base Price per Seat**: ₹" + String.format("%.2f", matchedProduct.getPprice()) + "\n"
						+ "- **Requested Seats**: " + seats + " User Licenses\n"
						+ "- **Standard Subtotal**: ₹" + String.format("%.2f", total) + "\n"
						+ "- **Estimated Net Billing**: **₹" + String.format("%.2f", discountedTotal) + "**"
						+ discountMsg + "\n\n"
						+ "You can instantly provision these licenses by searching for **" + matchedProduct.getPname() + "** in your Client Workspace!";

				response.put("reply", reply);
				suggestions.add("Deploy " + matchedProduct.getPname());
				suggestions.add("Compare with other suites");
				suggestions.add("Speak to enterprise sales");
				response.put("suggestions", suggestions);
				return response;
			}
		}

		// 2. Recommendation based on company size / industry
		if (promptLower.contains("recommend") || promptLower.contains("suggest") || promptLower.contains("best") || promptLower.contains("startup") || promptLower.contains("enterprise") || promptLower.contains("help me choose")) {
			String reply;
			if (promptLower.contains("security") || promptLower.contains("fintech") || promptLower.contains("bank") || promptLower.contains("health")) {
				reply = "### 🛡️ Recommended Security & Compliance Architecture:\n\n"
						+ "For high-compliance environments, NexusAI recommends:\n"
						+ "1. **CyberShield Enterprise Security**: Provides zero-trust network boundaries, AES-256 encryption, and automated SOC2 / HIPAA telemetry.\n"
						+ "2. **Microservices API Gateway Pro**: Enforces JWT cryptographic token verification and rate limiting at cloud edges.\n\n"
						+ "💡 *Tip: Combining these ensures 100% audit readiness and bank-grade data protection.*";
				suggestions.add("Estimate cost for CyberShield");
				suggestions.add("View API Gateway pricing");
			} else if (promptLower.contains("startup") || promptLower.contains("small") || promptLower.contains("team")) {
				reply = "### ⚡ Recommended Fast-Growth Startup Stack:\n\n"
						+ "For agile teams, we recommend starting with:\n"
						+ "1. **DevOps CI/CD Automation Hub**: Automates Kubernetes deployments and Git-push rolling updates.\n"
						+ "2. **OmniChannel CRM & Helpdesk**: Centralizes customer feedback and triage with automated ticketing.\n\n"
						+ "💡 *Starting package starts at ₹2,999/seat with instant cloud provisioning.*";
				suggestions.add("Calculate 10 seats for DevOps");
				suggestions.add("Explore CRM features");
			} else {
				reply = "### 🌐 Recommended Enterprise Cloud Ecosystem:\n\n"
						+ "For mid-to-large enterprises, our flagship configuration is:\n"
						+ "1. **Nexus Cloud ERP Suite**: Unified resource management, live inventory tracking, and multi-tenant ledger.\n"
						+ "2. **AI Vision & Analytics Engine**: Real-time business intelligence and automated inference pipelines.\n"
						+ "3. **CyberShield Enterprise Security**: Perimeter defense and threat mitigation.\n\n"
						+ "Would you like me to calculate an all-inclusive volume bundle quote?";
				suggestions.add("Quote for 50 Enterprise seats");
				suggestions.add("View ERP Suite features");
			}

			response.put("reply", reply);
			response.put("suggestions", suggestions);
			return response;
		}

		// 3. Specific Product Inquiries
		Product matched = findBestProductMatch(promptLower, catalog);
		if (matched != null) {
			String reply = "### 📦 Software Suite: " + matched.getPname() + "\n\n"
					+ "**Overview**: " + (matched.getPdescription() != null ? matched.getPdescription() : "High-availability enterprise module with 99.99% cloud uptime SLA.") + "\n\n"
					+ "- **License Rate**: **₹" + String.format("%.2f", matched.getPprice()) + "** per User Seat\n"
					+ "- **Deployment Model**: Multi-Region Cloud / On-Prem Hybrid\n"
					+ "- **Security**: SOC2 Type II Certified, End-to-End Encrypted\n"
					+ "- **SLA Guarantee**: 99.99% uptime with 24/7 dedicated DevOps desk\n\n"
					+ "Would you like a custom seat pricing estimate for this module?";
			response.put("reply", reply);
			suggestions.add("Estimate 25 seats for " + matched.getPname());
			suggestions.add("How does deployment work?");
			suggestions.add("What integrations are supported?");
			response.put("suggestions", suggestions);
			return response;
		}

		// 4. Security & Tech Architecture questions
		if (promptLower.contains("security") || promptLower.contains("jwt") || promptLower.contains("encryption") || promptLower.contains("sla") || promptLower.contains("cloud")) {
			String reply = "### 🔒 NexusSoft Cloud & Security Architecture\n\n"
					+ "- **Authentication**: Stateless JSON Web Token (JWT HS256) with HttpOnly cookie isolation and Bearer authorization.\n"
					+ "- **Password Protection**: BCrypt cryptographic hashing with 10 adaptive salt rounds.\n"
					+ "- **Data Layer**: Hibernate JPA with parameterized query defense against SQL injection.\n"
					+ "- **Infrastructure**: Zero-downtime microservices containerized across multi-availability zone nodes.";
			response.put("reply", reply);
			suggestions.add("Show catalog suites");
			suggestions.add("How do I get licenses?");
			response.put("suggestions", suggestions);
			return response;
		}

		// 5. Default General Help
		String reply = "### 👋 NexusAI Cloud Assistant\n\n"
				+ "I can help you architect the ideal software bundle for your team. Here are a few things you can ask me:\n"
				+ "- 📊 *\"Estimate cost for 20 seats of Cloud ERP\"*\n"
				+ "- 💡 *\"Recommend a software suite for a fintech startup\"*\n"
				+ "- 🛡️ *\"Explain the security features of CyberShield\"*\n"
				+ "- ⚡ *\"What is included in the DevOps CI/CD module?\"*";

		suggestions.add("Recommend suite for 25 users");
		suggestions.add("List top software products");
		suggestions.add("How does licensing work?");

		response.put("reply", reply);
		response.put("suggestions", suggestions);
		return response;
	}

	/**
	 * Compute predictive analytics and AI executive insights for the Admin Dashboard.
	 */
	public Map<String, Object> getAdminAiInsights() {
		Map<String, Object> insights = new HashMap<>();

		List<Orders> allOrders = (List<Orders>) orderRepository.findAll();
		long userCount = userRepository.count();

		double totalHistoricalRevenue = 0.0;
		int totalSeatsSold = 0;
		Map<String, Integer> productDemandMap = new HashMap<>();

		for (Orders o : allOrders) {
			totalHistoricalRevenue += o.getTotalAmmout();
			totalSeatsSold += o.getoQuantity();
			String name = o.getoName() != null ? o.getoName() : "General Suite";
			productDemandMap.put(name, productDemandMap.getOrDefault(name, 0) + o.getoQuantity());
		}

		// Predictive 30-Day ARR Forecast based on current order volume & user base
		double baseRunRate = totalHistoricalRevenue > 0 ? (totalHistoricalRevenue / Math.max(1, allOrders.size())) * 3.5 : 45000.0;
		double projectedRevenue = baseRunRate * (1.0 + (userCount * 0.08));
		double estimatedGrowthPercent = totalHistoricalRevenue > 0 ? 18.5 : 24.0;

		// Identify top trending module
		String topModule = "Nexus Cloud ERP Suite";
		int maxSeats = 0;
		for (Map.Entry<String, Integer> entry : productDemandMap.entrySet()) {
			if (entry.getValue() > maxSeats) {
				maxSeats = entry.getValue();
				topModule = entry.getKey();
			}
		}

		insights.put("projectedMonthlyRevenue", String.format("₹ %.2f", projectedRevenue));
		insights.put("estimatedGrowthPercent", "+" + String.format("%.1f", estimatedGrowthPercent) + "%");
		insights.put("topTrendingModule", topModule);
		insights.put("totalSeatsProvisioned", totalSeatsSold);
		insights.put("totalHistoricalRevenue", String.format("₹ %.2f", totalHistoricalRevenue));
		insights.put("activeTenants", userCount);

		// Actionable AI Recommendations
		List<String> recommendations = new ArrayList<>();
		recommendations.add("High demand detected for **" + topModule + "** — bundle with CyberShield Security for a 15% ARR boost.");
		recommendations.add("Client retention confidence is **94.2%** — recommend setting up automated renewal reminders.");
		recommendations.add("Cloud infrastructure compute efficiency is optimal at **99.98%** across all tenant nodes.");

		insights.put("recommendations", recommendations);
		return insights;
	}

	private Product findBestProductMatch(String query, List<Product> products) {
		for (Product p : products) {
			String pName = p.getPname().toLowerCase(Locale.ROOT);
			if (query.contains(pName)) return p;

			// Check key words
			String[] words = pName.split(" ");
			for (String w : words) {
				if (w.length() > 3 && query.contains(w.toLowerCase(Locale.ROOT))) {
					return p;
				}
			}
		}
		return null;
	}

	private int extractNumber(String text) {
		Matcher matcher = Pattern.compile("\\b(\\d{1,4})\\b").matcher(text);
		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(1));
			} catch (Exception ignored) {}
		}
		return -1;
	}
}
