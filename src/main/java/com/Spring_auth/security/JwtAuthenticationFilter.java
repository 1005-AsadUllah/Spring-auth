package com.Spring_auth.security;

import com.Spring_auth.enitity.User;
import com.Spring_auth.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String header =  request.getHeader("Authorization");
       log.info("Authorization header is {}", header);

       if(header != null && header.startsWith("Bearer ")){

           // Extract the token
           String token = header.substring(7);
           log.info("JWT Token: {}", token);


           try{

               if(!jwtService.isAccessToken(token)){
                   return;
               }
               Jws<Claims> parse =  jwtService.parse(token);
               Claims payload = parse.getPayload();

               Long userId = Long.valueOf(payload.getSubject());

               // validate it
               userRepository.findById(userId).ifPresent(user -> {
                   if(user.isEnabled()){
                       List<GrantedAuthority> authorities = user.getRoles() == null ? List.of() :
                               user.getRoles().stream()
                                       .map(role -> new SimpleGrantedAuthority(role.getRole()))
                                       .collect(Collectors.toUnmodifiableList());

                       UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                               user,
                               null,
                               authorities
                       );

                       authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       // set authentication in the security context

                       if(SecurityContextHolder.getContext().getAuthentication() == null)
                           SecurityContextHolder.getContext().setAuthentication(authentication);
                   }

               });


           }catch (ExpiredJwtException e){
               log.info("JWT Token has expired{}", e.getMessage());

           }catch (MalformedJwtException e){
                e.printStackTrace();
           }catch (JwtException e){
               e.printStackTrace();
           }catch (Exception e){
                e.printStackTrace();
           }


       }

       filterChain.doFilter(request, response);
    }
}
