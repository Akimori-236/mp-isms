package tfip.akimori.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import tfip.akimori.server.exceptions.UnauthorizedException;
import tfip.akimori.server.repositories.StoreRepository;

@Service
public class QrService {
    private static final String ANGULAR_BORROW_URL = "https://mp-server-production.up.railway.app/#/borrow/";
    private static final String GOQR_URL = "https://api.qrserver.com/v1/create-qr-code/";
    private static final String QR_COLOR_HEX = "00a";

    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private MongoLoggingService logSvc;

    private String buildURL(String data) {
        return UriComponentsBuilder.fromUriString(GOQR_URL)
                .queryParam("data", data)
                .queryParam("color", QR_COLOR_HEX)
                .build()
                .toUriString();
    }

    public ResponseEntity<byte[]> getQRResponse(String URLString) {
        RestTemplate template = new RestTemplate();
        // SET Headers
        final HttpHeaders headers = new HttpHeaders();

        // GET request creation with headers
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        // SEND GET REQUEST
        ResponseEntity<byte[]> response = template.exchange(URLString, HttpMethod.GET, entity, byte[].class);
        return response;
    }

    public byte[] getLoanQR(String instrumentID, String storeID, String jwt) throws UnauthorizedException {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // check manager clearance
        // verify store manager
        if (storeRepo.isManagerOfStore(email, storeID)) {
            // record approval of loan in mongo (w expiry)
            logSvc.approveLoan(instrumentID, email);
            // access external api
            String loanURL = ANGULAR_BORROW_URL + instrumentID;
            ResponseEntity<byte[]> response = getQRResponse(buildURL(loanURL));
            // get body from GET response
            byte[] imageBytes = response.getBody();
            return imageBytes;
        } else {
            // not a manager of the store
            throw new UnauthorizedException("Not a manager of store");
        }
    }

    // private String buildURL(String data, Integer pixelSize) throws Exception {
    // if (pixelSize <= 1000) {
    // return UriComponentsBuilder.fromUriString(GOQR_URL)
    // .queryParam("data", data)
    // .queryParam("size", pixelSize.toString() + "x" + pixelSize.toString())
    // .build()
    // .toUriString();
    // } else {
    // throw new Exception("Pixel size needs to be 1000 or below");
    // }
    // }

}
