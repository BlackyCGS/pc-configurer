package by.pcconf.pcconfigurer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
        List<T> items;
        Long totalItems;
        Integer pageNumber;
        Integer pageSize;
        Double totalPages;
}
