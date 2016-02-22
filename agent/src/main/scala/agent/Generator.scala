package agent

import java.lang.management.ManagementFactory
import java.util.Date
import com.sun.management.OperatingSystemMXBean


sealed trait MemoryType

case object Total extends MemoryType

case object Used extends MemoryType


object Generator {

	private val osMBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean.asInstanceOf[OperatingSystemMXBean]
	private val mb = 1024 * 1024

	def time = new Date().getTime

	def totalMemory = osMBean.getTotalPhysicalMemorySize / mb

	def freeMemory = osMBean.getFreePhysicalMemorySize / mb

	def usedMemory = totalMemory - freeMemory

	def metricCPU(name: String): MetricDTO = {
		MetricDTO(name, time, osMBean.getSystemCpuLoad)
	}

	def metricMemory(name: String, memoryType: MemoryType): MetricDTO = {
		val memoryValue = memoryType match {
			case Total => totalMemory
			case Used => usedMemory
		}
		MetricDTO(name, time, memoryValue)
	}
}