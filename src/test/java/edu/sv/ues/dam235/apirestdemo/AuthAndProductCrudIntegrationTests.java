package edu.sv.ues.dam235.apirestdemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sv.ues.dam235.apirestdemo.configs.JwtFilter;
import edu.sv.ues.dam235.apirestdemo.dtos.LoginDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthAndProductCrudIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtFilter jwtFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private static String jwtToken;
    private static Integer createdProductCode;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(jwtFilter)
                .build();
    }

    @Test
    @Order(1)
    void testAccessWithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    void testRegisterNewUser_Success() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail("newuser@temp.com");
        registerDTO.setPassword("Pass123.");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msj").value("Usuario registrado exitosamente"));
    }

    @Test
    @Order(3)
    void testRegisterDuplicateUser_ReturnsBadRequest() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Test");
        registerDTO.setLastName("User");
        registerDTO.setEmail("newuser@temp.com");
        registerDTO.setPassword("Pass123.");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msj").value("No se pudo registrar el usuario (correo ya existente o datos inválidos)"));
    }

    @Test
    @Order(4)
    void testLoginWithNewUser_ReturnsJwtToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUser("newuser@temp.com");
        loginDTO.setPass("Pass123.");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = jsonNode.get("token").asText();
        assertNotNull(jwtToken);
    }

    @Test
    @Order(5)
    void testGetAllProducts_WithValidToken_ReturnsList() throws Exception {
        assertNotNull(jwtToken);
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(6)
    void testGetAllProducts_SpanishAlias_ReturnsList() throws Exception {
        assertNotNull(jwtToken);
        mockMvc.perform(get("/productos")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    void testCreateProduct_Success() throws Exception {
        assertNotNull(jwtToken);
        ProductsDTO newProduct = new ProductsDTO();
        newProduct.setName("Laptop Gaming ASUS ROG");
        newProduct.setStatus(true);

        MvcResult result = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.name").value("Laptop Gaming ASUS ROG"))
                .andExpect(jsonPath("$.status").value(true))
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        createdProductCode = jsonNode.get("code").asInt();
        assertNotNull(createdProductCode);
    }

    @Test
    @Order(8)
    void testGetProductById_Success() throws Exception {
        assertNotNull(createdProductCode);
        mockMvc.perform(get("/products/" + createdProductCode)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(createdProductCode))
                .andExpect(jsonPath("$.name").value("Laptop Gaming ASUS ROG"));
    }

    @Test
    @Order(9)
    void testUpdateProduct_Success() throws Exception {
        assertNotNull(createdProductCode);
        ProductsDTO updateDTO = new ProductsDTO();
        updateDTO.setName("Laptop Gaming ASUS ROG Strix SCAR");
        updateDTO.setStatus(false);

        mockMvc.perform(put("/products/" + createdProductCode)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(createdProductCode))
                .andExpect(jsonPath("$.name").value("Laptop Gaming ASUS ROG Strix SCAR"))
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @Order(10)
    void testDeleteProduct_Success() throws Exception {
        assertNotNull(createdProductCode);
        mockMvc.perform(delete("/products/" + createdProductCode)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msj").value("Producto eliminado correctamente"));

        // Verificar que ya no existe (404)
        mockMvc.perform(get("/products/" + createdProductCode)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    void testLogout_ReturnsSuccess() throws Exception {
        assertNotNull(jwtToken);
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msj").value("Sesión cerrada correctamente"));
    }
}
