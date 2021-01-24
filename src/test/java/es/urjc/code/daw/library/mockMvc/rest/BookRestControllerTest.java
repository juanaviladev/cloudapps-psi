package es.urjc.code.daw.library.mockMvc.rest;

import es.urjc.code.daw.library.book.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String booksUrl = "/api/books/";
    private Book book;

    @BeforeTestClass
    public void init() {
        this.book = new Book("LA GUERRA DE LAS GALAXIAS",
                "Serie de peliculas de George Lucas donde se narra la vida de los jedi");
    }

    @Test
    public void testGivenNonRegisteredUserWhenGetAllBooksThenCanAccessBookList() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get(this.booksUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    public void testGivenNonRegisteredUserWhenTryToAddNewBookThenReturnStatusForbidden() throws Exception {
        String addBookUrl = "/api/books/";

        Book book = new Book("LA GUERRA DE LAS GALAXIAS",
                "Serie de peliculas de George Lucas donde se narra la vida de los jedi");

        this.mockMvc.perform(
                MockMvcRequestBuilders.post(this.booksUrl, this.book)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

}