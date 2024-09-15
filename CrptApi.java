package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.stream.IntStream;

public class CrptApi {
    private final Semaphore semaphore;
    private final Gson gson;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.gson = new Gson();
        this.semaphore = new Semaphore(requestLimit);

        long timeInterval = timeUnit.toMillis(1);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> semaphore.release(requestLimit - semaphore.availablePermits()),
                timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    public synchronized void createDocument(Document document, String signature) throws InterruptedException, IOException {
        semaphore.acquire();

        String stringJson = gson.toJson(document);

        URL url = new URL("https://ismp.crpt.ru/api/v3/1k/documents/create");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Signature", signature);
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = stringJson.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        System.out.println("Response code: " + connection.getResponseCode());
    }

    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 15);

        int numberOfDocuments = 1000;

        IntStream.range(0, numberOfDocuments)
                .forEach(i -> {
                    try {
                        api.createDocument(DocumentBuilder.buildRandomDocument(), "signature");
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public static class Document {
        private String description;
        private String docType;
        private String docStatus;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private String productionDate;
        private String productionType;
        private List<Product> products;
        private String regDate;
        private String regNumber;

        public String description() {
            return this.description;
        }

        public void description(String description) {
            this.description = description;
        }

        public String docType() {
            return this.docType;
        }

        public void docType(String docType) {
            this.docType = docType;
        }

        public String docStatus() {
            return this.docStatus;
        }

        public void docStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public boolean importRequest() {
            return this.importRequest;
        }

        public void importRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String ownerInn() {
            return this.ownerInn;
        }

        public void ownerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String participantInn() {
            return this.participantInn;
        }

        public void participantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public String producerInn() {
            return this.producerInn;
        }

        public void producerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public String productionDate() {
            return this.productionDate;
        }

        public void productionDate(String productionDate) {
            this.productionDate = productionDate;
        }

        public String productionType() {
            return this.productionType;
        }

        public void productionType(String productionType) {
            this.productionType = productionType;
        }

        public List<Product> products() {
            return this.products;
        }

        public void products(List<Product> products) {
            this.products = products;
        }

        public String regDate() {
            return this.regDate;
        }

        public void regDate(String regDate) {
            this.regDate = regDate;
        }

        public String regNumber() {
            return this.regNumber;
        }

        public void regNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        public static class Product {
            private String certificateDocument;
            private String certificateDocumentDate;
            private String certificateDocumentNumber;
            private String ownerInn;
            private String producerInn;
            private String productionDate;
            private String tnvedCode;
            private String uitCode;
            private String uituCode;

            public String certificateDocument() {
                return this.certificateDocument;
            }

            public void certificateDocument(String certificateDocument) {
                this.certificateDocument = certificateDocument;
            }

            public String certificateDocumentDate() {
                return this.certificateDocumentDate;
            }

            public void certificateDocumentDate(String certificateDocumentDate) {
                this.certificateDocumentDate = certificateDocumentDate;
            }

            public String certificateDocumentNumber() {
                return this.certificateDocumentNumber;
            }

            public void certificateDocumentNumber(String certificateDocumentNumber) {
                this.certificateDocumentNumber = certificateDocumentNumber;
            }

            public String ownerInn() {
                return this.ownerInn;
            }

            public void ownerInn(String ownerInn) {
                this.ownerInn = ownerInn;
            }

            public String producerInn() {
                return this.producerInn;
            }

            public void producerInn(String producerInn) {
                this.producerInn = producerInn;
            }

            public String productionDate() {
                return this.productionDate;
            }

            public void productionDate(String productionDate) {
                this.productionDate = productionDate;
            }

            public String tnvedCode() {
                return this.tnvedCode;
            }

            public void tnvedCode(String tnvedCode) {
                this.tnvedCode = tnvedCode;
            }

            public String uitCode() {
                return this.uitCode;
            }

            public void uitCode(String uitCode) {
                this.uitCode = uitCode;
            }

            public String uituCode() {
                return this.uituCode;
            }

            public void uituCode(String uituCode) {
                this.uituCode = uituCode;
            }
        }
    }

    public static class DocumentBuilder {
        private static final Random RANDOM = new Random();
        public static Document buildRandomDocument() {
            Document document = new Document();

            document.description(randomString());
            document.docType(randomString());
            document.docStatus(randomString());
            document.importRequest(RANDOM.nextBoolean());
            document.ownerInn(randomString());
            document.participantInn(randomString());
            document.producerInn(randomString());
            document.productionDate(randomDate());
            document.productionType(randomString());
            document.products(buildRandomProducts());
            document.regDate(randomDate());
            document.regNumber(randomString());

            return document;
        }

        private static List<Document.Product> buildRandomProducts() {
            List<Document.Product> products = new ArrayList<>();

            int productCount = RANDOM.nextInt(5) + 1;
            for (int i = 0; i < productCount; i++) {
                Document.Product product = new Document.Product();

                product.certificateDocument(randomString());
                product.certificateDocumentDate(randomDate());
                product.certificateDocumentNumber(randomString());
                product.ownerInn(randomString());
                product.producerInn(randomString());
                product.productionDate(randomDate());
                product.tnvedCode(randomString());
                product.uitCode(randomString());
                product.uituCode(randomString());
                products.add(product);
            }

            return products;
        }

        private static String randomString() {
            int length = RANDOM.nextInt(5) + 5;
            StringBuilder stringBuilder = new StringBuilder(length);

            for (int i = 0; i < length; i++) {
                stringBuilder.append((char) (RANDOM.nextInt(26) + 'a'));
            }

            return stringBuilder.toString();
        }

        private static String randomDate() {
            int year = RANDOM.nextInt(24) + 2000;
            int month = RANDOM.nextInt(12) + 1;
            int day = RANDOM.nextInt(28) + 1;

            return String.format("%04d-%02d-%02d", year, month, day);
        }
    }
}
