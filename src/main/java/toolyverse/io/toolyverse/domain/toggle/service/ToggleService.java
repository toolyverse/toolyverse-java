//package toolyverse.io.toolyverse.domain.toggle.service;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import toolyverse.io.toolyverse.domain.toggle.model.dto.EnvironmentDto;
//import toolyverse.io.toolyverse.domain.toggle.model.dto.ToggleWithEnvironmentsDto;
//import toolyverse.io.toolyverse.domain.toggle.repository.ToggleRepository;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ToggleService {
//
//    private final ToggleRepository toggleRepository;
//
//    /**
//     * Find all toggles with their environments populated efficiently (with pagination)
//     */
//    public Page<ToggleWithEnvironmentsDto> findAllTogglesWithEnvironments(Pageable pageable) {
//        // Step 1: Your existing query correctly fetches the paginated toggle data.
//        Page<ToggleWithEnvironmentsDto> togglesPage = toggleRepository.findAllTogglesWithEnvironments(pageable);
//
//        if (togglesPage.isEmpty()) {
//            return togglesPage;
//        }
//
//        // Step 2: Extract the IDs of the toggles on the current page.
//        List<Long> toggleIds = togglesPage.getContent().stream()
//                .map(ToggleWithEnvironmentsDto::getId)
//                .collect(Collectors.toList());
//
//        // Step 3  here fetch environment lookups by key
//        //todo
//
//        // Step 4: Populate the 'environments' list in your main DTOs.
//        togglesPage.getContent().forEach(toggle ->
//                toggle.setEnvironments(environmentsMap.getOrDefault(toggle.getId(), Collections.emptyList()))
//        );
//
//        return togglesPage;
//    }
//}