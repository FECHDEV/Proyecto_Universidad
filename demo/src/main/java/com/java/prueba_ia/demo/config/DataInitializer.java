package com.java.prueba_ia.demo.config;

import com.java.prueba_ia.demo.entity.*;
import com.java.prueba_ia.demo.repository.BookRepository;
import com.java.prueba_ia.demo.repository.LoanRepository;
import com.java.prueba_ia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@biblioteca.com")
                .fullName("Administrador")
                .role(Role.ADMIN)
                .build());

        User user1 = userRepository.save(User.builder()
                .username("user1")
                .password(passwordEncoder.encode("password1"))
                .email("user1@biblioteca.com")
                .fullName("Usuario Uno")
                .role(Role.USER)
                .build());

        User user2 = userRepository.save(User.builder()
                .username("user2")
                .password(passwordEncoder.encode("password2"))
                .email("user2@biblioteca.com")
                .fullName("Usuario Dos")
                .role(Role.USER)
                .build());

        Book book1 = bookRepository.save(Book.builder()
                .titulo("Cien Años de Soledad")
                .autor("Gabriel García Márquez")
                .isbn("978-3-16-148410-0")
                .codigoQr(UUID.randomUUID().toString())
                .genero("Novela")
                .anioPublicacion(1967)
                .ejemplaresDisponibles(5)
                .build());

        Book book2 = bookRepository.save(Book.builder()
                .titulo("1984")
                .autor("George Orwell")
                .isbn("978-0-45-152493-5")
                .codigoQr(UUID.randomUUID().toString())
                .genero("Distopía")
                .anioPublicacion(1949)
                .ejemplaresDisponibles(3)
                .build());

        Book book3 = bookRepository.save(Book.builder()
                .titulo("El Principito")
                .autor("Antoine de Saint-Exupéry")
                .isbn("978-0-15-601219-5")
                .codigoQr(UUID.randomUUID().toString())
                .genero("Infantil")
                .anioPublicacion(1943)
                .ejemplaresDisponibles(2)
                .build());

        Book book4 = bookRepository.save(Book.builder()
                .titulo("Don Quijote de la Mancha")
                .autor("Miguel de Cervantes")
                .isbn("978-0-14-243723-0")
                .codigoQr(UUID.randomUUID().toString())
                .genero("Clásico")
                .anioPublicacion(1605)
                .ejemplaresDisponibles(4)
                .build());

        Book book5 = bookRepository.save(Book.builder()
                .titulo("Fundación")
                .autor("Isaac Asimov")
                .isbn("978-0-55-329335-7")
                .codigoQr(UUID.randomUUID().toString())
                .genero("Ciencia Ficción")
                .anioPublicacion(1951)
                .ejemplaresDisponibles(0)
                .build());

        loanRepository.save(Loan.builder()
                .user(user1)
                .book(book2)
                .fechaPrestamo(LocalDateTime.now().minusDays(5))
                .fechaMaximaDevolucion(LocalDateTime.now().plusDays(2))
                .extensiones(0)
                .estado(EstadoPrestamo.ACTIVO)
                .build());

        loanRepository.save(Loan.builder()
                .user(user2)
                .book(book1)
                .fechaPrestamo(LocalDateTime.now().minusDays(10))
                .fechaMaximaDevolucion(LocalDateTime.now().minusDays(3))
                .fechaDevolucion(LocalDateTime.now().minusDays(3))
                .extensiones(0)
                .estado(EstadoPrestamo.DEVUELTO)
                .build());
    }
}
