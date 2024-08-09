//package com.santi.messagesp2p;
//
//import com.santi.messagesp2p.controller.UserController;
//import com.santi.messagesp2p.model.User;
//import com.santi.messagesp2p.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultMatcher;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
//public class UserControllerTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @MockBean
//  private UserService userService;
//
//  @Test
//  public void testGetAllUsers() throws Exception {
//    mockMvc.perform(get("/api/users"))
//        .andExpect(status().isOk())
//        .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.length()").value(0)); // asumiendo que inicialmente no hay usuarios
//  }
//
//  @Test
//  public void testGetUserByUsername() throws Exception {
//    User user = new User();
//    user.setId(1L);
//    user.setUsername("johndoe");
//    user.setEmail("johndoe@example.com");
//
//    Mockito.when(userService.findByUsername("johndoe")).thenReturn(user);
//
//    mockMvc.perform(get("/api/users/johndoe"))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.username").value("johndoe"))
//        .andExpect(jsonPath("$.email").value("johndoe@example.com"));
//  }
//
//  @Test
//  public void testCreateUser() throws Exception {
//    User user = new User();
//    user.setId(1L);
//    user.setUsername("johndoe");
//    user.setEmail("johndoe@example.com");
//    user.setPassword("secret");
//
//    Mockito.when(userService.saveUser(Mockito.any(User.class))).thenReturn(user);
//
//    String json = "{\"username\":\"johndoe\",\"email\":\"johndoe@example.com\",\"password\":\"secret\"}";
//
//    mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
//            .contentType("application/json")
//            .content(json))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.username").value("johndoe"))
//        .andExpect(jsonPath("$.email").value("johndoe@example.com"));
//  }
//}
