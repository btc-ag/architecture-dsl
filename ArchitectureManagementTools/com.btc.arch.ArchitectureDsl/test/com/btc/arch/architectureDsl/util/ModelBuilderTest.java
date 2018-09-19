package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;

public class ModelBuilderTest {

	@Test
	public void testNewModel() {
		// TODO use dummy module creator
		final ModelBuilder builder = new ModelBuilder(
				new DefaultModuleCreator());

		final String[] moduleNames = new String[] { "M1", "M2" };
		final Module createdModule0 = builder.getOrCreateModule(moduleNames[0]), createdModule1 = builder
				.getOrCreateModule(moduleNames[1]), retrievedModule0 = builder
				.getOrCreateModule(moduleNames[0]);
		assertNotSame(createdModule0, createdModule1);
		assertSame(createdModule0, retrievedModule0);
		final Model model = builder.toModel();
		assertEquals(2, model.getModules().size());
	}

	@Test
	public void testExistingModel() {
		final ModelBuilder builder = new ModelBuilder(
				new DefaultModuleCreator());

		final String[] moduleNames = new String[] { "M1", "M2", "M3" };
		final Module createdModule0 = builder.getOrCreateModule(moduleNames[0]), createdModule1 = builder
				.getOrCreateModule(moduleNames[1]);

		final Model model = builder.toModel();

		final ModelBuilder builder2 = new ModelBuilder(
				new DefaultModuleCreator(), model);
		final Module retrievedModule0 = builder2
				.getOrCreateModule(moduleNames[0]), retrievedModule1 = builder2
				.getOrCreateModule(moduleNames[1]), createdModule2 = builder2
				.getOrCreateModule(moduleNames[2]);

		assertSame(createdModule0, retrievedModule0);
		assertSame(createdModule1, retrievedModule1);
		assertNotSame(createdModule0, createdModule2);
		assertNotSame(createdModule1, createdModule2);
		final Model model2 = builder2.toModel();
		assertEquals(3, model2.getModules().size());

	}

	@Test
	public void testExistingModelAndOtherModel() {
		final ModelBuilder builder = new ModelBuilder(
				new DefaultModuleCreator());
		final ModelBuilder builder2 = new ModelBuilder(
				new DefaultModuleCreator());

		final String[] moduleNames = new String[] { "M1", "M2", "M3" };
		final Module createdModule0 = builder.getOrCreateModule(moduleNames[0]), createdModule1 = builder2
				.getOrCreateModule(moduleNames[1]);

		final Model model = builder.toModel();
		final Model model2 = builder2.toModel();

		final ModelBuilder builder3 = new ModelBuilder(
				new DefaultModuleCreator(), model, Arrays.asList(model, model2));

		final Module retrievedModule0 = builder3
				.getOrCreateModule(moduleNames[0]);
		final Module retrievedModule1 = builder3
				.getOrCreateModule(moduleNames[1]);
		final Module createdModule2 = builder3
				.getOrCreateModule(moduleNames[2]);

		assertSame(createdModule0, retrievedModule0);
		assertSame(createdModule1, retrievedModule1);
		assertNotSame(createdModule0, createdModule2);
		assertNotSame(createdModule1, createdModule2);

		final Model model3 = builder3.toModel();
		assertEquals(2, model3.getModules().size());
		assertArrayEquals(new Module[] { retrievedModule0, createdModule2 },
				model3.getModules().toArray());
	}

}
