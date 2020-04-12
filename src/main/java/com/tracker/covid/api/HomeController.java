package com.tracker.covid.api;

import com.tracker.covid.models.ConfirmedData;
import com.tracker.covid.models.Fatalities;
import com.tracker.covid.models.RecoveredData;
import com.tracker.covid.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final Logger logger = Logger.getLogger(HomeController.class.getName());

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<ConfirmedData> allConfirmed = coronaVirusDataService.getAllConfirmed();
        List<Fatalities> allFatalities = coronaVirusDataService.getAllDeaths();
        List<RecoveredData> allRecovered = coronaVirusDataService.getAllRecovered();
        int totalReportedCases = allConfirmed.stream().mapToInt(ConfirmedData::getLatestConfirmedCases).sum();
        int totalFatalities = allFatalities.stream().mapToInt(Fatalities::getLatestDeaths).sum();
        int totalRecovered = allRecovered.stream().mapToInt(RecoveredData::getLatestRecovered).sum();
        double mortalityRate = (totalFatalities*1000.00)/totalReportedCases;
        String countryWithHighestConfirmedCases = allConfirmed
                .stream()
                .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getKey();
        int highestConfirmedCasesByCountry = allConfirmed
                .stream()
                .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getValue();
        int totalConfirmCasesIndia = allConfirmed
                .stream()
                .filter((name) -> (name.getCountry().equals("India")))
                .map(ConfirmedData::getLatestConfirmedCases)
                .findAny()
                .orElse(null);
        String countryWithHighestFatalities = allFatalities
                .stream()
                .collect(Collectors.groupingBy(Fatalities::getCountry, Collectors.summingInt(Fatalities::getLatestDeaths)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getKey();
        int highestFatalitiesByCountry = allFatalities
                .stream()
                .collect(Collectors.groupingBy(Fatalities::getCountry, Collectors.summingInt(Fatalities::getLatestDeaths)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getValue();
        int fatalitiesIndia = allFatalities
                .stream()
                .filter((name) -> (name.getCountry().equals("India")))
                .findAny()
                .map(Fatalities::getLatestDeaths)
                .orElse(null);
        String countryWithHighestRecoveredCases = allRecovered
                .stream()
                .collect(Collectors.groupingBy(RecoveredData::getCountry, Collectors.summingInt(RecoveredData::getLatestRecovered)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getKey();
        int highestRecoveredCasesByCountry = allRecovered
                .stream()
                .collect(Collectors.groupingBy(RecoveredData::getCountry, Collectors.summingInt(RecoveredData::getLatestRecovered)))
                .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                .get().getValue();
        int recoveredIndia = allRecovered
                .stream()
                .filter((name) -> (name.getCountry().equals("India")))
                .findAny()
                .map(RecoveredData::getLatestRecovered)
                .orElse(null);
        Map<String, Integer> topFiveHighCasesCountries = allConfirmed
                .stream()
                .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(
                        LinkedHashMap::new,
                        (map, item) -> map.put(item.getKey(), item.getValue()),
                        Map::putAll
                );
        Map<String, Integer> topFiveHighDeathsCountries = allFatalities
                .stream()
                .collect(Collectors.groupingBy(Fatalities::getCountry, Collectors.summingInt(Fatalities::getLatestDeaths)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(
                        LinkedHashMap::new,
                        (map, item) -> map.put(item.getKey(), item.getValue()),
                        Map::putAll
                );
        Map<String, Integer> topFiveHighRecoverCountries = allRecovered
                .stream()
                .collect(Collectors.groupingBy(RecoveredData::getCountry, Collectors.summingInt(RecoveredData::getLatestRecovered)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(
                        LinkedHashMap::new,
                        (map, item) -> map.put(item.getKey(), item.getValue()),
                        Map::putAll
                );
        model.addAttribute("confirmedCases", allConfirmed);
        model.addAttribute("fatalities", allFatalities);
        model.addAttribute("recoveredCases", allRecovered);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalFatalities", totalFatalities);
        model.addAttribute("totalRecovered", totalRecovered);
        model.addAttribute("mortalityRate", new DecimalFormat("00.00").format(mortalityRate));
        model.addAttribute("countryWithHighestConfirmedCases", countryWithHighestConfirmedCases);
        model.addAttribute("highestConfirmedCasesByCountry", highestConfirmedCasesByCountry);
        model.addAttribute("countryWithHighestFatalities", countryWithHighestFatalities);
        model.addAttribute("highestFatalitiesByCountry", highestFatalitiesByCountry);
        model.addAttribute("countryWithHighestRecoveredCases", countryWithHighestRecoveredCases);
        model.addAttribute("highestRecoveredCasesByCountry", highestRecoveredCasesByCountry);
        model.addAttribute("totalConfirmCasesIndia", totalConfirmCasesIndia);
        model.addAttribute("fatalitiesIndia", fatalitiesIndia);
        model.addAttribute("recoveredIndia", recoveredIndia);
        model.addAttribute("topFiveHighCasesCountries", topFiveHighCasesCountries);
        model.addAttribute("topFiveHighDeathsCountries", topFiveHighDeathsCountries);
        model.addAttribute("topFiveHighRecoverCountries", topFiveHighRecoverCountries);
        logger.info("Populating the model for display");
        return "home";
    }

    /*allConfirmed.stream().
    collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
            .forEach((s, confirmedData) -> logger.info(s + " " + confirmedData));
    List<Integer> sums = allConfirmed
            .stream()
            .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
            .values().stream().collect(Collectors.toList());
    Set<Map.Entry<String, Integer>> entries = allConfirmed
            .stream()
            .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
            .entrySet();
        allConfirmed
                .stream()
                .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
            .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
            .toString();
    Optional<Integer> max = sums.stream().max(Comparator.comparing(integer -> integer));
    Optional<Map.Entry<String, Integer>> maxEntry = entries.stream().max(Comparator.comparing(Map.Entry::getValue));
        logger.info(max.get().toString());
        logger.info(maxEntry.get().toString());
        logger.info(allConfirmed
                .stream()
                .collect(Collectors.groupingBy(ConfirmedData::getCountry, Collectors.summingInt(ConfirmedData::getLatestConfirmedCases)))
            .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
            .get().getKey());*/
}
