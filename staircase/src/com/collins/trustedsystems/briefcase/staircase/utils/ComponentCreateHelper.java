package com.collins.trustedsystems.briefcase.staircase.utils;

import org.eclipse.emf.ecore.EClass;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AbstractImplementation;
import org.osate.aadl2.AbstractSubcomponent;
import org.osate.aadl2.AbstractType;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.ProcessorImplementation;
import org.osate.aadl2.ProcessorSubcomponent;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.ThreadGroupSubcomponent;
import org.osate.aadl2.ThreadGroupType;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.VirtualProcessorImplementation;
import org.osate.aadl2.VirtualProcessorSubcomponent;
import org.osate.ui.dialogs.Dialog;

/**
 * This class provides methods for creating component features without knowing
 * the component type or implementation class in advance
 */
public class ComponentCreateHelper {


	public ComponentCreateHelper() {

	}

	public static EClass getTypeClass(ComponentCategory compCategory) {
		switch (compCategory.getValue()) {
		case ComponentCategory.ABSTRACT_VALUE:
			return Aadl2Package.eINSTANCE.getAbstractType();
		case ComponentCategory.BUS_VALUE:
			return Aadl2Package.eINSTANCE.getBusType();
		case ComponentCategory.DATA_VALUE:
			return Aadl2Package.eINSTANCE.getDataType();
		case ComponentCategory.DEVICE_VALUE:
			return Aadl2Package.eINSTANCE.getDeviceType();
		case ComponentCategory.MEMORY_VALUE:
			return Aadl2Package.eINSTANCE.getMemoryType();
		case ComponentCategory.PROCESS_VALUE:
			return Aadl2Package.eINSTANCE.getProcessType();
		case ComponentCategory.PROCESSOR_VALUE:
			return Aadl2Package.eINSTANCE.getProcessorType();
		case ComponentCategory.SUBPROGRAM_VALUE:
			return Aadl2Package.eINSTANCE.getSubprogramType();
		case ComponentCategory.SUBPROGRAM_GROUP_VALUE:
			return Aadl2Package.eINSTANCE.getSubprogramGroupType();
		case ComponentCategory.SYSTEM_VALUE:
			return Aadl2Package.eINSTANCE.getSystemType();
		case ComponentCategory.THREAD_VALUE:
			return Aadl2Package.eINSTANCE.getThreadType();
		case ComponentCategory.THREAD_GROUP_VALUE:
			return Aadl2Package.eINSTANCE.getThreadGroupType();
		default:
			return null;
		}
	}

	public static EClass getImplClass(ComponentCategory compCategory) {
		switch (compCategory.getValue()) {
		case ComponentCategory.ABSTRACT_VALUE:
			return Aadl2Package.eINSTANCE.getAbstractImplementation();
		case ComponentCategory.BUS_VALUE:
			return Aadl2Package.eINSTANCE.getBusImplementation();
		case ComponentCategory.DATA_VALUE:
			return Aadl2Package.eINSTANCE.getDataImplementation();
		case ComponentCategory.DEVICE_VALUE:
			return Aadl2Package.eINSTANCE.getDeviceImplementation();
		case ComponentCategory.MEMORY_VALUE:
			return Aadl2Package.eINSTANCE.getMemoryImplementation();
		case ComponentCategory.PROCESS_VALUE:
			return Aadl2Package.eINSTANCE.getProcessImplementation();
		case ComponentCategory.PROCESSOR_VALUE:
			return Aadl2Package.eINSTANCE.getProcessorImplementation();
		case ComponentCategory.SUBPROGRAM_VALUE:
			return Aadl2Package.eINSTANCE.getSubprogramImplementation();
		case ComponentCategory.SUBPROGRAM_GROUP_VALUE:
			return Aadl2Package.eINSTANCE.getSubprogramGroupImplementation();
		case ComponentCategory.SYSTEM_VALUE:
			return Aadl2Package.eINSTANCE.getSystemImplementation();
		case ComponentCategory.THREAD_VALUE:
			return Aadl2Package.eINSTANCE.getThreadImplementation();
		case ComponentCategory.THREAD_GROUP_VALUE:
			return Aadl2Package.eINSTANCE.getThreadGroupImplementation();
		default:
			return null;
		}
	}

	// Ports
	public static EventDataPort createOwnedEventDataPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedEventDataPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedEventDataPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedEventDataPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedEventDataPort();
		} else if (compType instanceof ThreadGroupType) {
			return ((ThreadGroupType) compType).createOwnedEventDataPort();
		} else {
			return null;
		}
	}

	public static void createOwnedEventDataPort(ComponentType compType, EventDataPort port) {
		if (compType instanceof SystemType) {
			((SystemType) compType).getOwnedEventDataPorts().add(port);
		} else if (compType instanceof AbstractType) {
			((AbstractType) compType).getOwnedEventDataPorts().add(port);
		} else if (compType instanceof ProcessType) {
			((ProcessType) compType).getOwnedEventDataPorts().add(port);
		} else if (compType instanceof ThreadType) {
			((ThreadType) compType).getOwnedEventDataPorts().add(port);
		} else if (compType instanceof ThreadGroupType) {
			((ThreadGroupType) compType).getOwnedEventDataPorts().add(port);
		} else {
			return;
		}
	}

	public static DataPort createOwnedDataPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedDataPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedDataPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedDataPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedDataPort();
		} else if (compType instanceof ThreadGroupType) {
			return ((ThreadGroupType) compType).createOwnedDataPort();
		} else {
			return null;
		}
	}

	public static void createOwnedDataPort(ComponentType compType, DataPort port) {
		if (compType instanceof SystemType) {
			((SystemType) compType).getOwnedDataPorts().add(port);
		} else if (compType instanceof AbstractType) {
			((AbstractType) compType).getOwnedDataPorts().add(port);
		} else if (compType instanceof ProcessType) {
			((ProcessType) compType).getOwnedDataPorts().add(port);
		} else if (compType instanceof ThreadType) {
			((ThreadType) compType).getOwnedDataPorts().add(port);
		} else if (compType instanceof ThreadGroupType) {
			((ThreadGroupType) compType).getOwnedDataPorts().add(port);
		} else {
			return;
		}
	}

	public static EventPort createOwnedEventPort(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedEventPort();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedEventPort();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedEventPort();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedEventPort();
		} else if (compType instanceof ThreadGroupType) {
			return ((ThreadGroupType) compType).createOwnedEventPort();
		} else {
			return null;
		}
	}

	public static void createOwnedEventPort(ComponentType compType, EventPort port) {
		if (compType instanceof SystemType) {
			((SystemType) compType).getOwnedEventPorts().add(port);
		} else if (compType instanceof AbstractType) {
			((AbstractType) compType).getOwnedEventPorts().add(port);
		} else if (compType instanceof ProcessType) {
			((ProcessType) compType).getOwnedEventPorts().add(port);
		} else if (compType instanceof ThreadType) {
			((ThreadType) compType).getOwnedEventPorts().add(port);
		} else if (compType instanceof ThreadGroupType) {
			((ThreadGroupType) compType).getOwnedEventPorts().add(port);
		} else {
			return;
		}
	}

	public static DataAccess createOwnedDataAccess(ComponentType compType) {
		if (compType instanceof SystemType) {
			return ((SystemType) compType).createOwnedDataAccess();
		} else if (compType instanceof AbstractType) {
			return ((AbstractType) compType).createOwnedDataAccess();
		} else if (compType instanceof ProcessType) {
			return ((ProcessType) compType).createOwnedDataAccess();
		} else if (compType instanceof ThreadType) {
			return ((ThreadType) compType).createOwnedDataAccess();
		} else if (compType instanceof ThreadGroupType) {
			return ((ThreadGroupType) compType).createOwnedDataAccess();
		} else {
			return null;
		}
	}

	public static void createOwnedDataAccess(ComponentType compType, DataAccess access) {
		if (compType instanceof SystemType) {
			((SystemType) compType).getOwnedDataAccesses().add(access);
		} else if (compType instanceof AbstractType) {
			((AbstractType) compType).getOwnedDataAccesses().add(access);
		} else if (compType instanceof ProcessType) {
			((ProcessType) compType).getOwnedDataAccesses().add(access);
		} else if (compType instanceof ThreadType) {
			((ThreadType) compType).getOwnedDataAccesses().add(access);
		} else if (compType instanceof ThreadGroupType) {
			((ThreadGroupType) compType).getOwnedDataAccesses().add(access);
		} else {
			return;
		}
	}

	public static void createOwnedSubprogramAccess(ComponentType compType, SubprogramAccess access) {
		if (compType instanceof SystemType) {
			((SystemType) compType).getOwnedSubprogramAccesses().add(access);
		} else if (compType instanceof AbstractType) {
			((AbstractType) compType).getOwnedSubprogramAccesses().add(access);
		} else if (compType instanceof ProcessType) {
			((ProcessType) compType).getOwnedSubprogramAccesses().add(access);
		} else if (compType instanceof ThreadType) {
			((ThreadType) compType).getOwnedSubprogramAccesses().add(access);
		} else if (compType instanceof ThreadGroupType) {
			((ThreadGroupType) compType).getOwnedSubprogramAccesses().add(access);
		} else {
			return;
		}
	}

	// Annex subclause
	public static DefaultAnnexSubclause createOwnedAnnexSubclause(Classifier component) {
		if (component instanceof SystemType) {
			return ((SystemType) component).createOwnedAnnexSubclause();
		} else if (component instanceof AbstractType) {
			return ((AbstractType) component).createOwnedAnnexSubclause();
		} else if (component instanceof ProcessType) {
			return ((ProcessType) component).createOwnedAnnexSubclause();
		} else if (component instanceof ThreadGroupType) {
			return ((ThreadGroupType) component).createOwnedAnnexSubclause();
		} else if (component instanceof ThreadType) {
			return ((ThreadType) component).createOwnedAnnexSubclause();
		} else if (component instanceof SystemImplementation) {
			return ((SystemImplementation) component).createOwnedAnnexSubclause();
		} else if (component instanceof AbstractImplementation) {
			return ((AbstractImplementation) component).createOwnedAnnexSubclause();
		} else if (component instanceof ProcessImplementation) {
			return ((ProcessImplementation) component).createOwnedAnnexSubclause();
		} else if (component instanceof ThreadGroupImplementation) {
			return ((ThreadGroupImplementation) component).createOwnedAnnexSubclause();
		} else if (component instanceof ThreadImplementation) {
			return ((ThreadImplementation) component).createOwnedAnnexSubclause();
		} else {
			return null;
		}
	}

//	// Realization
//	public static Realization createOwnedRealization(ComponentImplementation compImpl) {
//		if (compImpl instanceof SystemImplementation) {
//			return ((SystemImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof AbstractImplementation) {
//			return ((AbstractImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof ProcessImplementation) {
//			return ((ProcessImplementation) compImpl).createOwnedRealization();
//		} else if (compImpl instanceof ThreadImplementation) {
//			return ((ThreadImplementation) compImpl).createOwnedRealization();
//		} else {
//			return null;
//		}
//	}

	// Subcomponent
	public static Subcomponent createOwnedSubcomponent(ComponentImplementation compImpl,
			ComponentCategory compCategory) {
		if (compImpl instanceof SystemImplementation) {
			if (compCategory == ComponentCategory.SYSTEM) {
				return ((SystemImplementation) compImpl).createOwnedSystemSubcomponent();
			} else if (compCategory == ComponentCategory.ABSTRACT) {
				return ((SystemImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.PROCESS) {
				return ((SystemImplementation) compImpl).createOwnedProcessSubcomponent();
			} else if (compCategory == ComponentCategory.VIRTUAL_PROCESSOR) {
				return ((SystemImplementation) compImpl).createOwnedVirtualProcessorSubcomponent();
			}
		} else if (compImpl instanceof AbstractImplementation) {
			if (compCategory == ComponentCategory.SYSTEM) {
				return ((AbstractImplementation) compImpl).createOwnedSystemSubcomponent();
			} else if (compCategory == ComponentCategory.ABSTRACT) {
				return ((AbstractImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.PROCESS) {
				return ((AbstractImplementation) compImpl).createOwnedProcessSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD) {
				return ((AbstractImplementation) compImpl).createOwnedThreadSubcomponent();
			}
		} else if (compImpl instanceof ProcessImplementation) {
			if (compCategory == ComponentCategory.ABSTRACT) {
				return ((ProcessImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD) {
				return ((ProcessImplementation) compImpl).createOwnedThreadSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD_GROUP) {
				return ((ProcessImplementation) compImpl).createOwnedThreadGroupSubcomponent();
			}
		} else if (compImpl instanceof ProcessorImplementation) {
			if (compCategory == ComponentCategory.ABSTRACT) {
				return ((ProcessorImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.VIRTUAL_PROCESSOR) {
				return ((ProcessorImplementation) compImpl).createOwnedVirtualProcessorSubcomponent();
			}
		} else if (compImpl instanceof ThreadGroupImplementation) {
			if (compCategory == ComponentCategory.ABSTRACT) {
				return ((ThreadGroupImplementation) compImpl).createOwnedAbstractSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD) {
				return ((ThreadGroupImplementation) compImpl).createOwnedThreadSubcomponent();
			} else if (compCategory == ComponentCategory.THREAD_GROUP) {
				return ((ThreadGroupImplementation) compImpl).createOwnedThreadGroupSubcomponent();
			}
		}

		Dialog.showError("Component Create Helper",
					"Create Owned Subcomponent: unhandled component type (" + compImpl.getTypeName() + ")");
		return null;
	}

	public static void setSubcomponentType(Subcomponent subcomponent, ComponentImplementation compImpl) {
		if (subcomponent instanceof SystemSubcomponent) {
			((SystemSubcomponent) subcomponent).setSystemSubcomponentType((SystemImplementation) compImpl);
		} else if (subcomponent instanceof AbstractSubcomponent) {
			((AbstractSubcomponent) subcomponent).setAbstractSubcomponentType((AbstractImplementation) compImpl);
		} else if (subcomponent instanceof ProcessSubcomponent) {
			((ProcessSubcomponent) subcomponent).setProcessSubcomponentType((ProcessImplementation) compImpl);
		} else if (subcomponent instanceof ThreadSubcomponent) {
			((ThreadSubcomponent) subcomponent).setThreadSubcomponentType((ThreadImplementation) compImpl);
		} else if (subcomponent instanceof ThreadGroupSubcomponent) {
			((ThreadGroupSubcomponent) subcomponent)
					.setThreadGroupSubcomponentType((ThreadGroupImplementation) compImpl);
		} else if (subcomponent instanceof ProcessorSubcomponent) {
			((ProcessorSubcomponent) subcomponent).setProcessorSubcomponentType((ProcessorImplementation) compImpl);
		} else if (subcomponent instanceof VirtualProcessorSubcomponent) {
			((VirtualProcessorSubcomponent) subcomponent)
					.setVirtualProcessorSubcomponentType((VirtualProcessorImplementation) compImpl);
		} else {
			Dialog.showError("Component Create Helper", "Set Subcomponent Type: unhandled subcomponent type ("
					+ subcomponent.getSubcomponentType().getName() + ")");
		}
	}

}
