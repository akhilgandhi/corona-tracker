package com.tracker.covid.services;

import com.tracker.covid.models.ConfirmedData;
import com.tracker.covid.models.Fatalities;
import com.tracker.covid.models.RecoveredData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CoronaVirusDataService {

    private final Logger logger = Logger.getLogger(CoronaVirusDataService.class.getName());

    private static String VIRUS_CONFIRM_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String VIRUS_DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String VIRUS_RECOVERED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private List<ConfirmedData> allConfirmed = new ArrayList<>();
    private List<Fatalities> allDeaths = new ArrayList<>();
    private List<RecoveredData> allRecovered = new ArrayList<>();

    public List<ConfirmedData> getAllConfirmed() {
        return allConfirmed;
    }

    public List<Fatalities> getAllDeaths() {
        return allDeaths;
    }

    public List<RecoveredData> getAllRecovered() {
        return allRecovered;
    }

    /**
     * This method will make http request on scheduled basis for getting the data for confirmed cases,
     * deaths and recovered cases every six hours.
     * @see <a href="https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data/csse_covid_19_time_series">CSSEGISandData</a>
     * @throws IOException
     * @throws InterruptedException
     */
    @PostConstruct
    @Scheduled(cron = "0 0 */6 * * *")
    public void getCoronaVirusData() throws IOException, InterruptedException {

        List<ConfirmedData> confirmedStats = new ArrayList<>();
        List<Fatalities> fatalStats = new ArrayList<>();
        List<RecoveredData> recoveredStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();

        //Getting Confirmed Cases
        HttpRequest requestConfirmedCases = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_CONFIRM_DATA_URL))
                .build();
        HttpResponse<String> httpResponseConfirmedCases = client.send(requestConfirmedCases, HttpResponse.BodyHandlers.ofString());
        logger.info("Fetched confirmed data");

        //Getting Fatalities
        HttpRequest requestFatalities = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DEATH_DATA_URL))
                .build();
        HttpResponse<String> httpResponseFatalities = client.send(requestFatalities, HttpResponse.BodyHandlers.ofString());
        logger.info("Fetched fatalities data");

        //Getting Recovered Cases
        HttpRequest requestRecovered = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_RECOVERED_DATA_URL))
                .build();
        HttpResponse<String> httpResponseRecovered = client.send(requestRecovered, HttpResponse.BodyHandlers.ofString());
        logger.info("Fetched recovered data");

        // Saving state for confirmed cases
        StringReader readerConfirmedCases = new StringReader(httpResponseConfirmedCases.body());
        Iterable<CSVRecord> confirmedRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerConfirmedCases);
        for (CSVRecord record : confirmedRecords) {
            ConfirmedData confirmedData = new ConfirmedData();
            confirmedData.setState(record.get("Province/State"));
            confirmedData.setCountry(record.get("Country/Region"));
            int latestConfirmedCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            confirmedData.setLatestConfirmedCases(latestConfirmedCases);
            confirmedData.setDiffFromPrevDay(latestConfirmedCases - prevDayDiff);
            confirmedData.setPercentDiff(new DecimalFormat("00.00").format(latestConfirmedCases == prevDayDiff ? 0.00 : (((latestConfirmedCases - prevDayDiff) * 100.00) / latestConfirmedCases)));
            confirmedStats.add(confirmedData);
        }
        this.allConfirmed = confirmedStats;
        logger.info("Populated statistical data for confirmed cases");

        // Saving state for fatalities
        StringReader readerFatalities = new StringReader(httpResponseFatalities.body());
        Iterable<CSVRecord> fatalRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerFatalities);
        for (CSVRecord record : fatalRecords) {
            Fatalities fatalities = new Fatalities();
            fatalities.setState(record.get("Province/State"));
            fatalities.setCountry(record.get("Country/Region"));
            int latestDeaths = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            fatalities.setLatestDeaths(latestDeaths);
            fatalities.setDiffFromPrevDay(latestDeaths - prevDayDiff);
            fatalities.setPercentDiff(new DecimalFormat("00.00").format(latestDeaths == prevDayDiff ? 0.00 : (((latestDeaths - prevDayDiff) * 100.00) / latestDeaths)));
            fatalStats.add(fatalities);
        }
        this.allDeaths = fatalStats;
        logger.info("Populated statistical data for fatalities");

        // Saving state for recovered cases
        StringReader readerRecovered = new StringReader(httpResponseRecovered.body());
        Iterable<CSVRecord> recoveredRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerRecovered);
        for (CSVRecord record : recoveredRecords) {
            RecoveredData recoveredData = new RecoveredData();
            recoveredData.setState(record.get("Province/State"));
            recoveredData.setCountry(record.get("Country/Region"));
            int latestRecovered = Integer.parseInt(record.get(record.size()-1));
            int prevDayDiff = Integer.parseInt(record.get(record.size()-2));
            recoveredData.setLatestRecovered(prevDayDiff);
            recoveredData.setDiffFromPrevDay(latestRecovered - prevDayDiff);
            recoveredData.setPercentDiff(new DecimalFormat("00.00").format(latestRecovered == prevDayDiff ? 0.00 : (((latestRecovered - prevDayDiff) * 100.00) / latestRecovered)));
            recoveredStats.add(recoveredData);
        }
        this.allRecovered = recoveredStats;
        logger.info("Populated statistical data for recovered cases");
    }
}
