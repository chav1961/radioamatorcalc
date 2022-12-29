package chav1961.calc;

import org.jocl.CL;

import org.jocl.*;
import static java.lang.System.nanoTime;


public class OpenCLWrapper implements AutoCloseable{
    /**
     * The source code of the OpenCL program to execute
     */
    private static String programSource =
    "__kernel void "+
    "matrixMul(__global float* A,"+ 
    "          __global float* B,"+ 
    "          __global float* C,"+ 
    "          int wA, int wB)"+
    "{"+
       "int tx = get_global_id(0);"+ 
       "int ty = get_global_id(1);"+
       "for (int i = 0; i < wA; i++) {"+
       "}"+
       "float value = 0;"+
       "for (int k = 0; k < wA; ++k)"+
       "{"+
       "   float elementA = A[ty * wA + k];"+
       "   float elementB = B[k * wB + tx];"+
       "   value += elementA * elementB;"+
       "}"+

      "C[ty * wA + tx] = value;"+
    "}";


    private final cl_context 		context;
    private final cl_command_queue	commandQueue;
    
    public OpenCLWrapper(final int platformIndex, final int deviceIndex) {
       // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_ALL, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_ALL, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        this.context = CL.clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);

        // Create a command-queue for the selected device
        this.commandQueue = CL.clCreateCommandQueue(context, device, 0, null);
    }

    public OpenCLExecutor getExecutor(final String programName, final String program, final int globalSize, final int localSize) {
    	return new OpenCLExecutor(programName, program, globalSize, localSize);
    }
    
    @Override
    public void close() throws RuntimeException {
    	CL.clReleaseCommandQueue(commandQueue);
    	CL.clReleaseContext(context);
   }
    
	public static void main(String args[]) {
	   	try(final OpenCLWrapper	t = new OpenCLWrapper(0, 0)) {
	        int n = 1000;
	        float srcArrayA[] = new float[n*n];
	        float srcArrayB[] = new float[n*n];
	        float dstArray[] = new float[n*n];
	        for (int i=0; i<n*n; i++) {
	            srcArrayA[i] = i;
	            srcArrayB[i] = i;
	        }
	        
	        try(final OpenCLExecutor	ex = t.getExecutor("matrixMul", programSource, n, 1)) {
	        	System.err.println("Duration1="+ex.execute(new SourceFloatArray(srcArrayA)
	        					, new SourceFloatArray(srcArrayB)
	        					, new TargetFloatArray(dstArray)
	        					, new SourceInt(n)
	        					, new SourceInt(n)
	        	));
	        	System.err.println("Duration2="+ex.execute(new SourceFloatArray(srcArrayA)
    					, new SourceFloatArray(srcArrayB)
    					, new TargetFloatArray(dstArray)
    					, new SourceInt(n)
    					, new SourceInt(n)
	        	));
	        }
	   	}
	}
    
   public class OpenCLExecutor implements AutoCloseable {
	   private final cl_program program;
	   private final cl_kernel	kernel;
	   private final long[]		global_work_size;
	   private final long[]		local_work_size;		
	   
	   public OpenCLExecutor(final String programName, final String source, final int globalSize, final int localSize) {
	        // Create the program from the source code
	        this.program = CL.clCreateProgramWithSource(context, 1, new String[]{ source }, null, null);

	        // Build the program
	        CL.clBuildProgram(program, 0, null, null, null, null);

	        // Create the kernel
	        this.kernel = CL.clCreateKernel(program, programName, null);

	        // Set the work-item dimensions
	        this.global_work_size = new long[]{globalSize};
	        this.local_work_size = new long[]{localSize};
	   }
	   
	   public long execute(final ExecutorArgument... parameters) {
	        final long 		time = nanoTime();
	        
	        // Allocate and fill buffers for arrays
	 	   	final cl_mem 	memObjects[] = new cl_mem[parameters.length];
	 	   	
	 	   	for(int index = 0; index < parameters.length; index++) {
	 	   		switch (parameters[index].getArgumentType()) {
					case FLOAT_ARRAY	:
						switch (parameters[index].getAccessMode()) {
							case IN	:
								final float[]	sourceContent = ((SourceFloatArray)parameters[index]).getContent();
						        
								memObjects[index] = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * sourceContent.length, Pointer.to(sourceContent), null);
								break;
							case OUT	:
								final float[]	targetContent = ((TargetFloatArray)parameters[index]).getContent();
								
						        memObjects[index] = CL.clCreateBuffer(context, CL.CL_MEM_READ_WRITE, Sizeof.cl_float * targetContent.length, null, null);
								break;
							case IN_OUT	:
							default:
								throw new UnsupportedOperationException("Access mode  ["+parameters[index].getAccessMode()+"] is not supported yet"); 
						}
						break;
					case INT_VALUE		:
						break;
					default :
						throw new UnsupportedOperationException("Argument type ["+parameters[index].getArgumentType()+"] is not supported yet"); 
	 	   		}
	 	   	}

	        // Set the arguments for the kernel
	 	   	for(int index = 0; index < parameters.length; index++) {
	 	   		switch (parameters[index].getArgumentType()) {
					case FLOAT_ARRAY	:
				        CL.clSetKernelArg(kernel, index, Sizeof.cl_mem, Pointer.to(memObjects[index]));
						break;
					case INT_VALUE		:
				        CL.clSetKernelArg(kernel, index, Sizeof.cl_int, Pointer.to(new int[] {((SourceInt)parameters[index]).getContent()}));
						break;
					default:
						throw new UnsupportedOperationException("Argument type ["+parameters[index].getArgumentType()+"] is not supported yet"); 
	 	   		}
	 	   	}
	        
	        CL.clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);

	        // Read the output data
	 	   	for(int index = 0; index < parameters.length; index++) {
	 	   		switch (parameters[index].getArgumentType()) {
					case FLOAT_ARRAY	:
						switch (parameters[index].getAccessMode()) {
							case IN		:
							case IN_OUT	:
								break;
							case OUT	:
								final float[]	targetContent = ((TargetFloatArray)parameters[index]).getContent();
								
						        CL.clEnqueueReadBuffer(commandQueue, memObjects[index], CL.CL_TRUE, 0, Sizeof.cl_float * targetContent.length, Pointer.to(targetContent), 0, null, null);
								break;
							default:
								break;
						}
						break;
					case INT_VALUE		:
						break;
					default:
						throw new UnsupportedOperationException("Argument type ["+parameters[index].getArgumentType()+"] is not supported yet"); 
	 	   		}
	 	   	}
	 	   	
	        // Release memory objects
	 	   	for (cl_mem item : memObjects) {
	 	   		if (item != null) {
	 		        CL.clReleaseMemObject(item);
	 	   		}
	 	   	}
	        return nanoTime() - time;
	   }

	@Override
	public void close() throws RuntimeException {
        // Release kernel and program
		CL.clReleaseKernel(kernel);
		CL.clReleaseProgram(program);
	}
   }
   
   private static enum ArgumentType {
	   FLOAT_ARRAY,
	   INT_VALUE
   }

   private static enum AccessMode {
	   IN,
	   OUT,
	   IN_OUT
   }
   
   static class ExecutorArgument {
	   private final ArgumentType 	argType;
	   private final AccessMode		access;
	   
	   ExecutorArgument(final ArgumentType type, final AccessMode accessMode) {
		   this.argType = type;
		   this.access = accessMode;
	   }
	   
	   ArgumentType getArgumentType() {
		   return argType;
	   }
	   
	   AccessMode getAccessMode() {
		   return access;
	   }
   }
   
   public static class SourceFloatArray extends ExecutorArgument {
	   private final float[]	content;

	   public SourceFloatArray(final float[] content) {
			super(ArgumentType.FLOAT_ARRAY, AccessMode.IN);
			this.content = content;
	   }

	   float[] getContent() {
		   return content;
	   }
   }

   public static class TargetFloatArray extends ExecutorArgument {
	   private final float[]	content;

	   public TargetFloatArray(final float[] content) {
			super(ArgumentType.FLOAT_ARRAY, AccessMode.OUT);
			this.content = content;
	   }
	   
	   float[] getContent() {
		   return content;
	   }
   }

   public static class SourceInt extends ExecutorArgument {
	   private final int	content;

	   public SourceInt(final int content) {
			super(ArgumentType.INT_VALUE, AccessMode.IN);
			this.content = content;
	   }
	   
	   int getContent() {
		   return content;
	   }
   }
}