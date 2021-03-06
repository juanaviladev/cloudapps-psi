package es.urjc.code.daw.library.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.urjc.code.daw.library.book.Book;
import es.urjc.code.daw.library.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String booksUrl = "/api/books/";
    private Book book;

    @BeforeEach
    public void init() {
        this.book = new Book("LA GUERRA DE LAS GALAXIAS",
                "Serie de peliculas de George Lucas donde se narra la vida de los jedi");
    }

    @Test
    public void testGivenNonRegisteredUserWhenGetAllBooksThenCanAccessBookList() throws Exception {
        when(this.bookService.findAll()).thenReturn(Collections.emptyList());
        this.mockMvc.perform(
                MockMvcRequestBuilders.get(this.booksUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    public void testGivenNonRegisteredUserWhenTryToAddNewBookThenReturnStatusForbidden() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post(this.booksUrl)
                        .content(this.objectMapper.writeValueAsString(this.book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"USER"})
    public void testGivenRegisteredUserWhenTryToAddNewBookThenTheBookIsSaved() throws Exception {
        when(this.bookService.save(ArgumentMatchers.any())).thenReturn(this.book);
        this.mockMvc.perform(
                MockMvcRequestBuilders.post(this.booksUrl)
                        .content(this.objectMapper.writeValueAsString(this.book))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", equalTo("LA GUERRA DE LAS GALAXIAS")));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"USER"})
    public void testGivenRegisteredUserButNoAdminWhenTryToDeleteBookThenReturnStatusForbidden() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete(this.booksUrl + "{id}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "pass", roles = {"USER", "ADMIN"})
    public void testGivenAdminUserWhenTryToDeleteBookWhichDoesntExistThenReturnNotFoundStatus() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(this.bookService).delete(anyLong());
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete(this.booksUrl + "{id}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", password = "pass", roles = {"USER", "ADMIN"})
    public void testGivenAdminUserWhenTryToDeleteBookThenTheBookIsDeleted() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete(this.booksUrl + "{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}