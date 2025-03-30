package dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import dto.user.User;

import java.util.ArrayList;

@lombok.Data
public class Page {

    private final Integer page;
    @JsonProperty("per_page")
    private final Integer perPage;
    private final Integer total;
    @JsonProperty("total_pages")
    private final Integer totalPages;
    private final ArrayList<User> data;

    public Page(Integer page, Integer perPage, Integer total, Integer totalPages, ArrayList<User> data) {
        this.page = page;
        this.perPage = perPage;
        this.total = total;
        this.totalPages = totalPages;
        this.data = data;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public ArrayList<User> getUser() {
        return data;
    }
}
