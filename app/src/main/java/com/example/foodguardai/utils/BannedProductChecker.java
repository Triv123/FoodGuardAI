package com.example.foodguardai.utils;

import com.example.foodguardai.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BannedProductChecker {

    // ─────────────────────────────────────────────
    // TIER 1 — BANNED IN INDIA (FSSAI)
    // ─────────────────────────────────────────────
    private static final Set<String> BANNED_IN_INDIA = new HashSet<>(Arrays.asList(
            // Synthetic food colors banned by FSSAI
            "rhodamine b", "metanil yellow", "malachite green",
            "sudan red", "sudan i", "sudan ii", "sudan iii", "sudan iv",
            "orange ii", "orange rn", "orange g",
            "butter yellow", "auramine", "chrysoidine",
            "fast yellow ab", "oil yellow ob", "oil yellow aab",
            "non-permitted coal tar dyes",

            // Preservatives banned in India
            "potassium bromate",        // bread improver, banned FSSAI 2016
            "brominated vegetable oil", // BVO — banned
            "formaldehyde",             // milk preservative — banned
            "boric acid", "borax",      // food preservative — banned
            "diethylene glycol",        // sweetener adulterant — banned
            "melamine",                 // protein adulterant — banned
            "lead compounds",
            "arsenic compounds",

            // Sweeteners
            "cyclamate", "dulcin", "p-4000",

            // Trans fat sources (restricted >2% in India since 2022)
            "partially hydrogenated vegetable oil",
            "partially hydrogenated oils",
            "partially hydrogenated fat",
            "vanaspati",

            // Adulterants commonly found in India
            "argemone oil",             // mustard oil adulterant
            "mineral oil",
            "kesari dal",               // lathyrus sativus — toxic
            "papaya seeds in black pepper",
            "starch in spices",
            "chalk powder",
            "urea in milk",
            "oxytocin",
            "calcium carbide",          // fruit ripening — banned

            // Pesticide residues (above limits)
            "endosulfan",
            "monocrotophos",
            "methyl parathion",
            "chlorpyrifos excess"
    ));

    // ─────────────────────────────────────────────
    // TIER 2 — BANNED IN USA (FDA)
    // ─────────────────────────────────────────────
    private static final Set<String> BANNED_IN_USA = new HashSet<>(Arrays.asList(
            "brominated vegetable oil",     // FDA revoked GRAS 2024
            "potassium bromate",
            "propylparaben",                // FDA removed from GRAS
            "red dye 3",                    // erythrosine — FDA ban 2024 (meat)
            "red 3",
            "erythrosine",
            "titanium dioxide",             // food use restricted
            "azodicarbonamide",             // flour bleaching agent
            "partially hydrogenated oils",  // PHOs — FDA banned 2018
            "rbst", "rbgh",                 // bovine growth hormone (labeling)
            "cyanuric acid",
            "melamine"
    ));

    // ─────────────────────────────────────────────
    // TIER 3 — BANNED IN EU
    // ─────────────────────────────────────────────
    private static final Set<String> BANNED_IN_EU = new HashSet<>(Arrays.asList(
            "azodicarbonamide",
            "brominated vegetable oil",
            "potassium bromate",
            "titanium dioxide",             // E171 — banned EU 2022
            "e171",
            "red 40", "allura red",         // restricted in EU (warning label)
            "yellow 5", "tartrazine",       // warning label required
            "yellow 6", "sunset yellow",
            "blue 1", "brilliant blue",
            "green 3", "fast green",
            "red 2", "amaranth",            // banned EU
            "cyclamate",
            "saccharin",                    // restricted
            "bha",                          // butylated hydroxyanisole — restricted
            "propyl gallate",
            "thiodipropionic acid",
            "4-hexylresorcinol",
            "carbamide",
            "quinine",                      // restricted in beverages
            "aspartame"                     // revaluated, some restrictions
    ));

    // ─────────────────────────────────────────────
    // TIER 4 — GLOBALLY CONTROVERSIAL / HARMFUL
    // ─────────────────────────────────────────────
    private static final Set<String> GLOBALLY_HARMFUL = new HashSet<>(Arrays.asList(
            // Artificial colors with ADHD links
            "red 40", "allura red ac",
            "yellow 5", "tartrazine",
            "yellow 6", "sunset yellow fcf",
            "blue 1", "brilliant blue fcf",
            "blue 2", "indigo carmine",
            "green 3",
            "red 3", "erythrosine",
            "caramel color iv",             // contains 4-methylimidazole
            "4-methylimidazole",

            // Preservatives with health concerns
            "sodium benzoate",              // forms benzene with vitamin C
            "bha", "butylated hydroxyanisole",
            "bht", "butylated hydroxytoluene",
            "tbhq", "tertiary butylhydroquinone",
            "sodium nitrate", "sodium nitrite",  // processed meats
            "potassium sorbate",            // mild concern
            "propionate", "calcium propionate",

            // Sweeteners with concerns
            "aspartame",                    // phenylalanine warning
            "saccharin",
            "acesulfame k", "acesulfame potassium",
            "sucralose",                    // gut microbiome effects
            "high fructose corn syrup",
            "hfcs",

            // Emulsifiers & stabilizers with concerns
            "carrageenan",                  // inflammatory
            "polysorbate 80", "e433",
            "polysorbate 60", "e435",
            "carboxymethylcellulose", "e466",
            "xanthan gum",                  // large amounts only

            // Flavor enhancers
            "monosodium glutamate", "msg",  // controversial
            "disodium inosinate", "e631",
            "disodium guanylate", "e627",
            "hydrolyzed vegetable protein", "hvp",
            "autolyzed yeast extract",

            // Other
            "brominated flour",
            "aluminium compounds",          // raising agents
            "sodium aluminium phosphate",
            "alum",
            "mineral oil hydrocarbon",
            "propylene glycol",             // carrier solvent
            "acetylated distarch adipate"
    ));

    // ─────────────────────────────────────────────
    // ALLERGENS (not banned but must flag)
    // ─────────────────────────────────────────────
    private static final Set<String> MAJOR_ALLERGENS = new HashSet<>(Arrays.asList(
            "peanut", "peanuts", "groundnut",
            "tree nuts", "almond", "cashew", "walnut", "pistachio",
            "milk", "dairy", "lactose", "casein", "whey",
            "egg", "eggs",
            "wheat", "gluten",
            "soy", "soya", "soybean",
            "fish", "shellfish", "crustacean",
            "sesame", "sesame seed",
            "mustard",
            "celery",
            "lupin",
            "molluscs", "sulphites", "sulphur dioxide"
    ));

    // ─────────────────────────────────────────────
    // E-NUMBER LOOKUP (common harmful ones)
    // ─────────────────────────────────────────────
    private static final Map<String, String> E_NUMBER_MAP = new HashMap<String, String>() {{
        put("e102", "Tartrazine (Yellow 5) — banned in Norway/Austria, ADHD link");
        put("e104", "Quinoline Yellow — banned in USA/Japan/Australia");
        put("e110", "Sunset Yellow — ADHD link, banned in Norway");
        put("e120", "Cochineal/Carmine — allergen risk");
        put("e122", "Carmoisine — banned in USA/Japan/Sweden");
        put("e123", "Amaranth — banned in USA since 1976");
        put("e124", "Ponceau 4R — banned in USA/Norway");
        put("e127", "Erythrosine (Red 3) — banned in USA food use 2024");
        put("e128", "Red 2G — banned in Australia/USA");
        put("e131", "Patent Blue V — banned in USA/Australia");
        put("e132", "Indigo Carmine — some restrictions");
        put("e133", "Brilliant Blue — banned in several countries");
        put("e142", "Green S — banned in USA/Canada/Japan");
        put("e150d", "Caramel Color IV — contains 4-MEI, carcinogen concern");
        put("e151", "Brilliant Black — banned in USA/Canada/Japan");
        put("e154", "Brown FK — banned in most countries");
        put("e155", "Brown HT — banned in USA/Canada/Japan");
        put("e171", "Titanium Dioxide — banned in EU 2022");
        put("e173", "Aluminium — restricted");
        put("e210", "Benzoic Acid — forms benzene with Vitamin C");
        put("e211", "Sodium Benzoate — ADHD concern, forms benzene");
        put("e212", "Potassium Benzoate — forms benzene");
        put("e216", "Propyl p-hydroxybenzoate — banned in EU food");
        put("e220", "Sulphur Dioxide — allergen, asthma trigger");
        put("e249", "Potassium Nitrite — carcinogenic in processed meat");
        put("e250", "Sodium Nitrite — carcinogenic in processed meat");
        put("e251", "Sodium Nitrate — carcinogenic concern");
        put("e320", "BHA — possible carcinogen, banned in Japan");
        put("e321", "BHT — possible carcinogen");
        put("e338", "Phosphoric Acid — bone density concern");
        put("e407", "Carrageenan — inflammatory gut concern");
        put("e433", "Polysorbate 80 — gut microbiome disruption");
        put("e466", "Carboxymethylcellulose — gut inflammation");
        put("e621", "MSG/Monosodium Glutamate — controversial");
        put("e951", "Aspartame — phenylalanine, cancer debate");
        put("e954", "Saccharin — bladder cancer in animals");
        put("e955", "Sucralose — gut microbiome effects");
        put("e924", "Potassium Bromate — banned in India/EU/Canada");
        put("e927b", "Carbamide/Urea — banned in several countries");
    }};

    // ─────────────────────────────────────────────
    // RESULT CLASSES
    // ─────────────────────────────────────────────

    public static class IngredientFlag {
        public final String ingredient;
        public final String severity;    // "BANNED_INDIA", "BANNED_USA", "BANNED_EU", "HARMFUL", "ALLERGEN"
        public final String reason;
        public final String bannedIn;

        public IngredientFlag(String ingredient, String severity, String reason, String bannedIn) {
            this.ingredient = ingredient;
            this.severity = severity;
            this.reason = reason;
            this.bannedIn = bannedIn;
        }

        public boolean isCritical() {
            return severity.equals("BANNED_INDIA") || severity.equals("BANNED_USA") || severity.equals("BANNED_EU");
        }
    }

    public static class IngredientAnalysisResult {
        public boolean hasBannedIngredients;
        public boolean hasHarmfulIngredients;
        public boolean hasAllergens;
        public List<IngredientFlag> flaggedIngredients;
        public List<String> safeIngredients;
        public int safetyDeduction;  // points to deduct from safety rating
        public String overallVerdict; // "SAFE", "CAUTION", "AVOID"

        public IngredientAnalysisResult() {
            flaggedIngredients = new ArrayList<>();
            safeIngredients = new ArrayList<>();
            safetyDeduction = 0;
        }
    }

    public static class BanStatus {
        private boolean isBanned;
        private String reason;

        public BanStatus(boolean isBanned, String reason) {
            this.isBanned = isBanned;
            this.reason = reason;
        }

        public boolean isBanned() { return isBanned; }
        public String getReason() { return reason; }
    }

    // ─────────────────────────────────────────────
    // MAIN ANALYSIS METHOD
    // ─────────────────────────────────────────────

    /**
     * Full ingredient analysis — call this with the ingredients string from API
     * e.g. "Sugar, Palm Oil, Cocoa Powder, Sodium Benzoate, E102, Tartrazine"
     */
    public static IngredientAnalysisResult analyzeIngredients(String ingredientsText) {
        IngredientAnalysisResult result = new IngredientAnalysisResult();

        if (ingredientsText == null || ingredientsText.trim().isEmpty()) {
            result.overallVerdict = "UNKNOWN";
            return result;
        }

        // Split by comma, semicolon, or newline
        String[] parts = ingredientsText.toLowerCase().split("[,;\\n]+");

        for (String raw : parts) {
            String ingredient = raw.trim()
                    .replaceAll("\\(.*?\\)", "") // remove parenthetical sub-ingredients
                    .replaceAll("[*†‡§]", "")     // remove footnote markers
                    .trim();

            if (ingredient.isEmpty()) continue;

            IngredientFlag flag = checkIngredient(ingredient);
            if (flag != null) {
                result.flaggedIngredients.add(flag);
                if (flag.severity.equals("BANNED_INDIA")) {
                    result.hasBannedIngredients = true;
                    result.safetyDeduction += 4;
                } else if (flag.severity.equals("BANNED_USA") || flag.severity.equals("BANNED_EU")) {
                    result.hasBannedIngredients = true;
                    result.safetyDeduction += 3;
                } else if (flag.severity.equals("HARMFUL")) {
                    result.hasHarmfulIngredients = true;
                    result.safetyDeduction += 1;
                } else if (flag.severity.equals("ALLERGEN")) {
                    result.hasAllergens = true;
                }
            } else {
                result.safeIngredients.add(capitalizeFirst(ingredient));
            }
        }

        // Cap deduction at 9
        result.safetyDeduction = Math.min(result.safetyDeduction, 9);

        // Overall verdict
        if (result.hasBannedIngredients) {
            result.overallVerdict = "AVOID";
        } else if (result.hasHarmfulIngredients || result.safetyDeduction >= 3) {
            result.overallVerdict = "CAUTION";
        } else if (result.hasAllergens) {
            result.overallVerdict = "ALLERGEN_WARNING";
        } else {
            result.overallVerdict = "SAFE";
        }

        return result;
    }

    private static IngredientFlag checkIngredient(String ingredient) {
        // 1. Check E-numbers first (exact match)
        for (Map.Entry<String, String> entry : E_NUMBER_MAP.entrySet()) {
            if (ingredient.contains(entry.getKey())) {
                String key = entry.getKey().toUpperCase();
                String desc = entry.getValue();
                String severity = desc.contains("banned in India") || desc.contains("banned in EU")
                        ? "BANNED_EU" : "HARMFUL";
                if (BANNED_IN_INDIA.contains(ingredient)) severity = "BANNED_INDIA";
                return new IngredientFlag(ingredient, severity, desc,
                        extractBannedIn(desc));
            }
        }

        // 2. Check banned in India
        for (String banned : BANNED_IN_INDIA) {
            if (ingredient.contains(banned)) {
                return new IngredientFlag(
                        capitalizeFirst(ingredient),
                        "BANNED_INDIA",
                        "Banned by FSSAI in India",
                        "🇮🇳 India"
                );
            }
        }

        // 3. Check banned in USA
        for (String banned : BANNED_IN_USA) {
            if (ingredient.contains(banned)) {
                return new IngredientFlag(
                        capitalizeFirst(ingredient),
                        "BANNED_USA",
                        "Banned or restricted by FDA (USA)",
                        "🇺🇸 USA"
                );
            }
        }

        // 4. Check banned in EU
        for (String banned : BANNED_IN_EU) {
            if (ingredient.contains(banned)) {
                return new IngredientFlag(
                        capitalizeFirst(ingredient),
                        "BANNED_EU",
                        "Banned or restricted in European Union",
                        "🇪🇺 EU"
                );
            }
        }

        // 5. Check globally harmful
        for (String harmful : GLOBALLY_HARMFUL) {
            if (ingredient.contains(harmful)) {
                return new IngredientFlag(
                        capitalizeFirst(ingredient),
                        "HARMFUL",
                        "Associated with health concerns globally",
                        "⚠️ Multiple countries"
                );
            }
        }

        // 6. Check allergens
        for (String allergen : MAJOR_ALLERGENS) {
            if (ingredient.contains(allergen)) {
                return new IngredientFlag(
                        capitalizeFirst(ingredient),
                        "ALLERGEN",
                        "Major allergen — check if you're sensitive",
                        "⚠️ Allergen"
                );
            }
        }

        return null; // ingredient is safe
    }

    // ─────────────────────────────────────────────
    // LEGACY METHOD (kept for compatibility)
    // ─────────────────────────────────────────────
    public static BanStatus checkIfBanned(Product product) {
        String productName = product.getProductName() != null ?
                product.getProductName().toLowerCase() : "";

        // Check ingredients string if product has it
        if (product.getIngredientsText() != null) {
            IngredientAnalysisResult result = analyzeIngredients(product.getIngredientsText());
            if (result.hasBannedIngredients) {
                return new BanStatus(true, "Contains ingredients banned in India or internationally");
            }
        }
        return new BanStatus(false, "");
    }

    public static boolean isIngredientBanned(String ingredient) {
        return BANNED_IN_INDIA.contains(ingredient.toLowerCase().trim());
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private static String extractBannedIn(String description) {
        if (description.contains("India")) return "🇮🇳 India";
        if (description.contains("EU")) return "🇪🇺 EU";
        if (description.contains("USA")) return "🇺🇸 USA";
        if (description.contains("Japan")) return "🇯🇵 Japan";
        return "⚠️ Multiple countries";
    }

    private static String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}