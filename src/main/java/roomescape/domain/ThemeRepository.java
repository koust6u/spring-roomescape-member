package roomescape.domain;

import java.util.List;

public interface ThemeRepository {
    List<Theme> findAll();
}