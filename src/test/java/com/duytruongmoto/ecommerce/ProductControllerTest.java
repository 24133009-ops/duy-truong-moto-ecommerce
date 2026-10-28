package com.duytruongmoto.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProductsEndpoint() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void testProductsWithCategoryDoBaoHo() throws Exception {
        mockMvc.perform(get("/products").param("category", "do-bao-ho"))
                .andExpect(status().isOk());
    }

    @Test
    void testProductsWithCategoryGearParts() throws Exception {
        mockMvc.perform(get("/products").param("category", "gear-parts"))
                .andExpect(status().isOk());
    }
}
