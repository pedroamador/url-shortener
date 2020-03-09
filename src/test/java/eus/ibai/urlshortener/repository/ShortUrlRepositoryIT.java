package eus.ibai.urlshortener.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import eus.ibai.urlshortener.entity.ShortUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
class ShortUrlRepositoryIT {

    private static final UUID ENABLED_ENTITY_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final UUID DISABLED_ENTITY_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Autowired
    private ShortUrlRepository repository;

    @Test
    void when_savingAnEntity_Then_ItCanBeRetrieved() {
        ShortUrl entity = ShortUrl.builder().key("key").url("url").enabled(true).build();

        entity = repository.save(entity);
        repository.flush();

        assertThat(entity.getId(), notNullValue());
        assertThat(entity.getCreatedOn(), notNullValue());
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void when_GettingAllEntites_Then_ReturnAllEnabled() {
        List<ShortUrl> list = repository.findAllEnabled();

        assertThat(list.size(), is(2));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void given_ExistingAndEnabledId_when_GettingById_Then_ReturnOne() {
        Optional<ShortUrl> optional = repository.findByIdIfEnabled(ENABLED_ENTITY_ID);

        assertThat(optional.isPresent(), is(true));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void given_ExistingAndDisabledId_when_GettingById_Then_ReturnZero() {
        Optional<ShortUrl> optional = repository.findByIdIfEnabled(DISABLED_ENTITY_ID);

        assertThat(optional.isPresent(), is(false));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void when_GettingExistingByKey_Then_ReturnOne() {
        Optional<ShortUrl> optional = repository.findByKey("AAA");

        assertThat(optional.isPresent(), is(true));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void when_GettingNonExistingById_Then_ReturnNothing() {
        Optional<ShortUrl> optional = repository.findById(DISABLED_ENTITY_ID);

        assertThat(optional.isEmpty(), is(true));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void given_AnEnabledEntity_When_DisablingIt_Then_ItIsDisabled() {
        int updated = repository.disableById(ENABLED_ENTITY_ID);

        assertThat(updated, is(1));
        ShortUrl entity = repository.findById(ENABLED_ENTITY_ID).orElseThrow();
        assertThat(entity.isEnabled(), is(false));
    }

    @Test
    @DatabaseSetup("/dataset/dataset.xml")
    void given_ADisabledEntity_When_DisablingIt_Then_DoNothing() {
        int updated = repository.disableById(DISABLED_ENTITY_ID);

        assertThat(updated, is(0));
    }
}