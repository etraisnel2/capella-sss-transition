/*******************************************************************************
 * Copyright (c) 2006, 2016 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.transition.system2subsystem.multiphases;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.core.data.capellacore.ModellingArchitecture;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.ctx.CtxPackage;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.la.LaPackage;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.pa.PaPackage;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.transition.common.constants.ITransitionConstants;
import org.polarsys.capella.core.transition.common.context.TransitionContext;
import org.polarsys.capella.transition.system2subsystem.crossphases.handlers.attachment.CrossPhasesAttachmentHelper;

public class MultiphasesContext extends TransitionContext {

  private Mapping mapping;

  public enum Mapping {

    SA("org.polarsys.capella.transition.system2subsystem.multiphases.sa", CtxPackage.Literals.SYSTEM_ANALYSIS), LA(
        "org.polarsys.capella.transition.system2subsystem.multiphases.la", LaPackage.Literals.LOGICAL_ARCHITECTURE), PA(
        "org.polarsys.capella.transition.system2subsystem.multiphases.pa", PaPackage.Literals.PHYSICAL_ARCHITECTURE);

    private final String mapping_id;
    private final EClass blockArchitecture;

    Mapping(String mapping, EClass blockArchitecture) {
      this.mapping_id = mapping;
      this.blockArchitecture = blockArchitecture;
    }

    public String getMappingId() {
      return mapping_id;
    }

    public EClass getBlockArchitectureClass() {
      return blockArchitecture;
    }

    private BlockArchitecture getTempBlockArchitecture(MultiphasesContext context) {
      SystemEngineering eng = context.getTempSystemEngineering();
      for (ModellingArchitecture ma : eng.getOwnedArchitectures()) {
        if (blockArchitecture.isInstance(ma)) {
          return (BlockArchitecture) ma;
        }
      }
      throw new IllegalStateException("No blockarchitecture of type '" + blockArchitecture.eClass().getName() + "' in temp system engineering! ");
    }

    private EObject getTempRootComponent(MultiphasesContext context) {
      BlockArchitecture ba = getTempBlockArchitecture(context);
      if (ba instanceof SystemAnalysis) {
        return ((SystemAnalysis) ba).getOwnedSystem();
      }
      if (ba instanceof LogicalArchitecture) {
        return ((LogicalArchitecture) ba).getOwnedLogicalComponent();
      }
      if (ba instanceof PhysicalArchitecture) {
        return ((PhysicalArchitecture) ba).getOwnedPhysicalComponent();
      }
      throw new IllegalStateException("No root component for " + blockArchitecture.eClass().getName());
    }
  }

  public MultiphasesContext(Collection<?> selection) {
    put(ITransitionConstants.TRANSPOSER_SELECTION, selection);
    put(ITransitionConstants.SAVE_REQUIRED, Boolean.TRUE);
  }

  /**
   * @return the selected physical node components.
   */
  @SuppressWarnings("unchecked")
public Collection<? extends PhysicalComponent> getSelectedPhysicalComponents() {
    return (Collection<? extends PhysicalComponent>) get(ITransitionConstants.TRANSITION_SOURCES);
  }

  @SuppressWarnings("rawtypes")
@Override
  /**
   * {@inheritDoc}
   */
  public void reset() {
	  	Collection incompleteElementsCollection = (Collection) this.get(ITransitionConstants.INCOMPLETE_ELEMENTS);
		 if (incompleteElementsCollection!=null) {
		 	incompleteElementsCollection.clear();
		 }
		 
		 Collection transformedElementsCollection = (Collection) this.get(ITransitionConstants.TRANSFORMED_ELEMENTS);
		 if (transformedElementsCollection!=null) {
			 transformedElementsCollection.clear();
		 }
		 
		 CrossPhasesAttachmentHelper.getInstance(this).clear(this);
	  }

  public void fullReset() {
	  super.reset();
  }

  /**
   * Not to be called by clients
   */
  public void setMapping(Mapping mapping) {
    this.mapping = mapping;
  }

  /**
   * @return the transformation mapping that is currently active
   */
  public Mapping getMapping() {
    return mapping;
  }

  /**
   * @return the temporary block architecture for the current mapping.
   */
  public BlockArchitecture getTempBlockArchitecture() {
    return mapping.getTempBlockArchitecture(this);
  }

  /**
   * @return the SystemEngineering of the temporary model
   */
  public SystemEngineering getTempSystemEngineering() {
    return (SystemEngineering) get(ITransitionConstants.TRANSFORMATION_TARGET_ROOT);
  }

  /**
   * @return the SystemAnalysis of the source model
   */
  public SystemAnalysis getSourceSystemAnalysis() {
    return getSourceSystemEngineering().getContainedSystemAnalysis().get(0);
  }

  /**
   * @return the LogicalArchitecture of the source model
   */
  public LogicalArchitecture getSourceLogicalArchitecture() {
    return getSourceSystemEngineering().getContainedLogicalArchitectures().get(0);
  }

  /**
   * @return the PhysicalArchitecture of the source model
   */
  public PhysicalArchitecture getSourcePhysicalArchitecture() {
    return getSourceSystemEngineering().getContainedPhysicalArchitectures().get(0);
  }

  /**
   * @return the SystemEngineering of the source model
   */
  public SystemEngineering getSourceSystemEngineering() {
    return (SystemEngineering) get(ITransitionConstants.TRANSFORMATION_SOURCE_ROOT);
  }

  /**
   * @return the SystemAnanlysis of the temporary model
   */
  public SystemAnalysis getTempSystemAnalysis() {
    return getTempSystemEngineering().getContainedSystemAnalysis().get(0);
  }

  /**
   * @return the LogicalArchitecture of the temporary model
   */
  public LogicalArchitecture getTempLogicalArchitecture() {
    return getTempSystemEngineering().getContainedLogicalArchitectures().get(0);
  }

  /**
   * @return the PhysicalArchitecture of the temporary model
   */
  public PhysicalArchitecture getTempPhysicalArchitecture() {
    return getTempSystemEngineering().getContainedPhysicalArchitectures().get(0);

  }

  /**
   * @return the root component of the temporary model for the current transformation phase
   */
  public EObject getTempRootComponent() {
    return mapping.getTempRootComponent(this);
  }

}
