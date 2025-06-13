package com.example.Order.Controller;


import com.example.Order.DTO.ProductDTO;
import com.example.Order.DTO.UserDTO;
import com.example.Order.Entity.Order;
import com.example.Order.Repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class Controller {

        @Autowired
        private Repository repo;

        @Autowired
        private RestTemplate restTemplate;

        @PostMapping("/add")
        public Order placeOrder(@RequestBody Order order) {
            // Get user
            String userUrl = "https://user62-andkcnfda0epetfd.centralus-01.azurewebsites.net/users/" + order.getUserId();
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(userUrl, UserDTO.class);
            if (!userResponse.getStatusCode().is2xxSuccessful()) return null;

            // Get product
            String productUrl = "https://inventory62-gmb9cefcguamgec9.eastus2-01.azurewebsites.net/products/" + order.getProductId();
            ResponseEntity<ProductDTO> productResponse = restTemplate.getForEntity(productUrl, ProductDTO.class);
            if (!productResponse.getStatusCode().is2xxSuccessful()) return null;

            ProductDTO product = productResponse.getBody();
            if (product.getStock() < order.getQuantity()) return null;

            // Update stock
            String updateStockUrl = "https://inventory62-gmb9cefcguamgec9.eastus2-01.azurewebsites.net/products/" + order.getProductId() + "/stock?stock=" + (product.getStock() - order.getQuantity());
            restTemplate.put(updateStockUrl, null);

            // Save order
            order.setTotalPrice(order.getQuantity() * product.getPrice());
            order.setStatus("PLACED");
            return repo.save(order);
        }

        @GetMapping("/all")
        public List<Order> getAll() {
            return repo.findAll();
        }

        @GetMapping("/{id}")
        public Order getOne(@PathVariable Long id) {
            return repo.findById(id).orElse(null);
        }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = repo.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

}

