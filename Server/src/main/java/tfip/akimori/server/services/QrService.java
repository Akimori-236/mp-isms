package tfip.akimori.server.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import tfip.akimori.server.exceptions.UnauthorizedException;
import tfip.akimori.server.repositories.StoreRepository;

@Service
public class QrService {
    // https://quickchart.io/qr?text=https%3A%2F%2Fism.up.railway.app%2F%23%2Fborrow%2F367377a1&size=200
    private static final String ANGULAR_BORROW_URL = "https%3A%2F%2Fism.up.railway.app%2F%23%2Fborrow%2F";
    private static final String QR_API_URL = "https://quickchart.io/qr";
    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private MongoService logSvc;

    private String buildURL(String data) {
        String url = UriComponentsBuilder.fromUriString(QR_API_URL)
                .queryParam("text", data)
                .queryParam("dark", 704621)
                .queryParam("size", 200)
                .build()
                .toUriString();
        // System.out.println(url);
        return url;
    }

    public ResponseEntity<byte[]> getQRResponse(String URLString) {
        System.out.println(URLString);
        RestTemplate template = new RestTemplate();
        // SET Headers
        // final HttpHeaders headers = new HttpHeaders();
        // GET request creation with headers
        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.GET, URI.create(URLString));
        // SEND GET REQUEST
        ResponseEntity<byte[]> response = template.exchange(requestEntity, byte[].class);
        return response;
    }

    public byte[] getLoanQR(String instrumentID, String storeID, String jwt) throws UnauthorizedException {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // check manager clearance
        // verify store manager
        if (storeRepo.isManagerOfStore(email, storeID)) {
            String loanURL = ANGULAR_BORROW_URL + instrumentID;
            // access external api
            ResponseEntity<byte[]> response = getQRResponse(buildURL(loanURL));
            logSvc.approveLoan(instrumentID, email);
            return response.getBody();
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
