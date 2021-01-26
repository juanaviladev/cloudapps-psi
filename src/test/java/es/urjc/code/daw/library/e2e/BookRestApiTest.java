package es.urjc.code.daw.library.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookRestApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + port;
    }

    @Test
    public void shouldReturnAllBooksWithANonLoggedUserRequest() {
        givenANonLoggedUser()
                .when()
                    .get("/api/books/")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(is(equalTo(allBooks())));
    }

    @Test
    public void shouldSaveABookWithAUserRequest() {
        Integer savedBookId =
                givenAUser()
                            .contentType(ContentType.JSON)
                            .body(sampleBook())
                        .when()
                            .post("/api/books/")
                        .then()
                            .statusCode(201)
                            .contentType(ContentType.JSON)
                            .extract()
                            .path("id");

        givenAUser()
                .when()
                    .get("/api/books/{id}/", savedBookId)
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(is(equalTo(sampleBookWithId(savedBookId))));

    }

    @Test
    public void shouldRemoveABookWithAnAdminRequest() {
        givenAnAdmin()
                .when()
                    .delete("/api/books/{id}/", sampleId())
                .then()
                    .statusCode(200)
                    .body(is(emptyString()));

        givenAnAdmin()
                .when()
                    .get("/api/books/{id}/", sampleId())
                .then()
                    .statusCode(404)
                    .body(is(emptyString()));

    }

    private static Integer sampleId() {
        return 1;
    }

    private static RequestSpecification givenAnAdmin() {
        return given()
                .auth()
                .basic("admin", "pass");
    }

    private static RequestSpecification givenAUser() {
        return given()
                .auth()
                .basic("user", "pass");
    }

    private static RequestSpecification givenANonLoggedUser() {
        return given();
    }

    private static String sampleBookWithId(Integer id) {
        return "{\"id\":" + id + ",\"title\":\"SUEÑOS DE ACERO Y NEON II\",\"description\":\"Los personajes que protagonizan este relato sobreviven en una sociedad en decadencia a la que, no obstante, lograrán devolver la posibilidad de un futuro. Año 2484. En un mundo dominado por las grandes corporaciones, solo un hombre, Jordi Thompson, detective privado deslenguado y vividor, pero de gran talento y sentido d...\"}";
    }

    private static String sampleBook() {
        return "{\"title\":\"SUEÑOS DE ACERO Y NEON II\",\"description\":\"Los personajes que protagonizan este relato sobreviven en una sociedad en decadencia a la que, no obstante, lograrán devolver la posibilidad de un futuro. Año 2484. En un mundo dominado por las grandes corporaciones, solo un hombre, Jordi Thompson, detective privado deslenguado y vividor, pero de gran talento y sentido d...\"}";
    }

    private static String allBooks() {
        return "[{\"id\":1,\"title\":\"SUEÑOS DE ACERO Y NEON\",\"description\":\"Los personajes que protagonizan este relato sobreviven en una sociedad en decadencia a la que, no obstante, lograrán devolver la posibilidad de un futuro. Año 2484. En un mundo dominado por las grandes corporaciones, solo un hombre, Jordi Thompson, detective privado deslenguado y vividor, pero de gran talento y sentido d...\"},{\"id\":2,\"title\":\"LA VIDA SECRETA DE LA MENTE\",\"description\":\"La vida secreta de la mentees un viaje especular que recorre el cerebro y el pensamiento: se trata de descubrir nuestra mente para entendernos hasta en los más pequeños rincones que componen lo que somos, cómo forjamos las ideas en los primeros días de vida, cómo damos forma a las decisiones que nos constituyen, cómo soñamos y cómo imaginamos, por qué sentimos ciertas emociones hacia los demás, cómo los demás influyen en nosotros, y cómo el cerebro se transforma y, con él, lo que somos.\"},{\"id\":3,\"title\":\"CASI SIN QUERER\",\"description\":\"El amor algunas veces es tan complicado como impredecible. Pero al final lo que más valoramos son los detalles más simples, los más bonitos, los que llegan sin avisar. Y a la hora de escribir sobre sentimientos, no hay nada más limpio que hacerlo desde el corazón. Y eso hace Defreds en este libro.\"},{\"id\":4,\"title\":\"TERMINAMOS Y OTROS POEMAS SIN TERMINAR\",\"description\":\"Recopilación de nuevos poemas, textos en prosa y pensamientos del autor. Un sabio dijo una vez: «Pocas cosas hipnotizan tanto en este mundo como una llama y como la luna, será porque no podemos cogerlas o porque nos iluminan en la penumbra». Realmente no sé si alguien dijo esta cita o me la acabo de inventar pero deberían de haberla escrito porque el poder hipnótico que ejercen esa mujer de rojo y esa dama blanca sobre el ser humano es digna de estudio.\"},{\"id\":5,\"title\":\"LA LEGIÓN PERDIDA\",\"description\":\"En el año 53 a. C. el cónsul Craso cruzó el Éufrates para conquistar Oriente, pero su ejército fue destrozado en Carrhae. Una legión entera cayó prisionera de los partos. Nadie sabe a ciencia cierta qué pasó con aquella legión perdida.150 años después, Trajano está a punto de volver a cruzar el Éufrates. ...\"}]";
    }
}
