package com.nutritrack.lifestyleservice.exception;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.nutritrack.lifestyleservice.dto.ErrorResponse;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests various Bad Request scenarios and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Setup is handled by @Mock annotations
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single field error")
    void testHandleValidationException_SingleFieldError() {
        // Arrange
        FieldError fieldError = new FieldError("lifestyleEntryRequest", "userId", "User ID is required");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals("Input validation failed. Please check the provided data.", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().get(0).contains("userId"));
        assertTrue(response.getBody().getDetails().get(0).contains("User ID is required"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple field errors")
    void testHandleValidationException_MultipleFieldErrors() {
        // Arrange
        FieldError error1 = new FieldError("lifestyleEntryRequest", "userId", "User ID is required");
        FieldError error2 = new FieldError("lifestyleEntryRequest", "type", "Type is required");
        FieldError error3 = new FieldError("lifestyleEntryRequest", "value", "Value must be positive");
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2, error3));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getDetails());
        assertEquals(3, response.getBody().getDetails().size());
        
        // Verify all field errors are included
        List<String> details = response.getBody().getDetails();
        assertTrue(details.stream().anyMatch(d -> d.contains("userId") && d.contains("User ID is required")));
        assertTrue(details.stream().anyMatch(d -> d.contains("type") && d.contains("Type is required")));
        assertTrue(details.stream().anyMatch(d -> d.contains("value") && d.contains("Value must be positive")));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with custom message")
    void testHandleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Invalid lifestyle type: INVALID_TYPE. Valid types are: STEPS, WATER, SLEEP";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException for timestamp validation")
    void testHandleIllegalArgumentException_TimestampValidation() {
        // Arrange
        String errorMessage = "Timestamp cannot be older than 1 year";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException for value validation")
    void testHandleIllegalArgumentException_ValueValidation() {
        // Arrange
        String errorMessage = "Steps value cannot exceed 100,000. Provided: 150000";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("cannot exceed 100,000"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException for entry not found")
    void testHandleIllegalArgumentException_EntryNotFound() {
        // Arrange
        String errorMessage = "Entry not found with id: 999";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Entry not found"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic message")
    void testHandleHttpMessageNotReadableException_GenericError() {
        // Arrange
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(null);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid Request", response.getBody().getError());
        assertEquals("Invalid JSON format or data type", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with deserialization error")
    void testHandleHttpMessageNotReadableException_DeserializationError() {
        // Arrange
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        Throwable cause = new RuntimeException("Cannot deserialize value of type java.lang.Long");
        when(exception.getCause()).thenReturn(cause);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid Request", response.getBody().getError());
        assertEquals("Invalid data format. Please check field types and values.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with JSON parse error")
    void testHandleHttpMessageNotReadableException_JsonParseError() {
        // Arrange
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        Throwable cause = new RuntimeException("JSON parse error: Unexpected character");
        when(exception.getCause()).thenReturn(cause);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid Request", response.getBody().getError());
        assertEquals("JSON syntax error. Please check the request body.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    @DisplayName("Should handle NullPointerException as generic exception")
    void testHandleGenericException_NullPointer() {
        // Arrange
        Exception exception = new NullPointerException("Null pointer encountered");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    @DisplayName("Should handle database exception as generic exception")
    void testHandleGenericException_DatabaseError() {
        // Arrange
        Exception exception = new RuntimeException("Database connection failed");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        // Generic message should not expose internal details
        assertFalse(response.getBody().getMessage().contains("Database"));
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
    }
}
