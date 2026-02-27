package com.clinic.client_service.application.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PagedResponse<T>{
	private List<T> content;  // lista de resultados de la página
    private int page;         // página actual (0-based)
    private int size;         // tamaño de página
    private long total; 
}
