package com.radixdlt.client.application.translate.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.radixdlt.client.application.translate.ShardedParticleStateId;
import com.radixdlt.client.atommodel.cru.CRUDataParticle;
import com.radixdlt.client.atommodel.rri.RRIParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.identifiers.RadixAddress;

public class CRUDataUpdateTest {
    @Test
    public void testCreateMapper() {
        RadixAddress address = RadixAddress.from("JEbhKQzBn4qJzWJFBbaPioA2GTeaQhuUjYWkanTE6N8VvvPpvM8");
        RRI rri = RRI.of(address, "TestData");
        byte[] data = new byte[128];
        Arrays.fill(data, (byte) 0xFE);
        CreateDataAction createDataAction = CreateDataAction.create(rri, data);
        CreateDataToParticleGroupsMapper mapper = new CreateDataToParticleGroupsMapper();
        List<ParticleGroup> particleGroups = mapper.mapToParticleGroups(createDataAction);
        assertThat(particleGroups).hasSize(1);
        ImmutableList<SpunParticle> spunParticles = particleGroups.get(0).getSpunParticles();
        assertThat(spunParticles.get(0).getParticle()).isInstanceOf(RRIParticle.class);
        assertThat(spunParticles.get(1).getParticle()).isInstanceOf(CRUDataParticle.class);
    }

    @Test
    public void testUpdateMapper() {
        RadixAddress address = RadixAddress.from("JEbhKQzBn4qJzWJFBbaPioA2GTeaQhuUjYWkanTE6N8VvvPpvM8");
        RRI rri = RRI.of(address, "TestData");
        byte[] data = new byte[128];
        Arrays.fill(data, (byte) 0xFE);
        UpdateDataAction updateDataAction = UpdateDataAction.create(rri, data);
        UpdateDataToParticleGroupsMapper mapper = new UpdateDataToParticleGroupsMapper();
        Set<ShardedParticleStateId> state = mapper.requiredState(updateDataAction);
        assertThat(state).hasSize(1);
    }

    @Test
    public void testUpdateAction() {
        RadixAddress address = RadixAddress.from("JEbhKQzBn4qJzWJFBbaPioA2GTeaQhuUjYWkanTE6N8VvvPpvM8");
        RRI rri = RRI.of(address, "TestData");
        byte[] data = new byte[128];
        Arrays.fill(data, (byte) 0xFE);
        UpdateDataAction updateDataAction = UpdateDataAction.create(rri, data);
        List<Particle> items = new ArrayList<>();
        CRUDataParticle oldData = new CRUDataParticle(rri, 0, new byte[64]);
        items.add(oldData);
        Stream<Particle> store = items.stream();
        UpdateDataToParticleGroupsMapper mapper = new UpdateDataToParticleGroupsMapper();
        List<ParticleGroup> groups = mapper.mapToParticleGroups(updateDataAction, store);
        assertThat(groups).hasSize(1);
        ImmutableList<SpunParticle> spunParticles = groups.get(0).getSpunParticles();
        assertThat(spunParticles.get(0).getParticle()).isInstanceOf(CRUDataParticle.class);
        assertThat(spunParticles.get(1).getParticle()).isInstanceOf(CRUDataParticle.class);
    }
}