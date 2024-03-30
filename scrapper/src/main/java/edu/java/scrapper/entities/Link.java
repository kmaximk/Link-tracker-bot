package edu.java.scrapper.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "link")
@NoArgsConstructor
public class Link {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    @Convert(converter = LinkConverter.class)
    private URI url;

    @Column(name = "last_check_time")
    private OffsetDateTime lastCheckTime;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updates_count")
    private Integer updatesCount;

    @ManyToMany(mappedBy = "linkList", cascade = CascadeType.PERSIST)
    private List<Chat> chatList;

    public Link(
        URI url,
        OffsetDateTime lastCheckTime,
        OffsetDateTime updatedAt,
        Integer updatesCount
    ) {
        this.url = url;
        this.lastCheckTime = lastCheckTime;
        this.updatedAt = updatedAt;
        this.updatesCount = updatesCount;
    }
}
