package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {


}
