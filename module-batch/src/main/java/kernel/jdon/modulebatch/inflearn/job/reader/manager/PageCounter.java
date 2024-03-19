package kernel.jdon.modulebatch.inflearn.job.reader.manager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageCounter {
    private int currentPage = 0;

    public void incrementPage() {
        currentPage++;
    }

    public void reset() {
        currentPage = 0;
    }
}

