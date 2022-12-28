package chav1961.calc;

import static org.jocl.CL.*;

import org.jocl.*;
import static java.lang.System.nanoTime;


public class Test4
{
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

       "float value = 0;"+
       "for (int k = 0; k < wA; ++k)"+
       "{"+
       "   float elementA = A[ty * wA + k];"+
       "   float elementB = B[k * wB + tx];"+
       "   value += elementA * elementB;"+
       "}"+

      "C[ty * wA + tx] = value;"+
    "}";



    /**
     * The entry point of this sample
     * 
     * @param args Not used
     */
    public static void main(String args[])
    {
        // Create input- and output data 
        int n = 1000;
        float srcArrayA[] = new float[n*n];
        float srcArrayB[] = new float[n*n];
        float dstArray[] = new float[n*n];
        for (int i=0; i<n*n; i++)
        {
            srcArrayA[i] = i;
            srcArrayB[i] = i;
        }
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);
        

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);

        // Create a command-queue for the selected device
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);

        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n*n, srcA, null);
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n*n, srcB, null);
        memObjects[2] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n*n, null, null);

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ programSource }, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "matrixMul", null);

        long time = nanoTime();
        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0, 
            Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1, 
            Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, 
            Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernel, 3, 
                Sizeof.cl_int, Pointer.to(new int[] {n}));
        clSetKernelArg(kernel, 4, 
                Sizeof.cl_int, Pointer.to(new int[] {n}));
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};


        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
        		n*n * Sizeof.cl_float, dst, 0, null, null);

        time = nanoTime() - time;
        System.out.println("GPU time: "+ time +"ns " + (time/1000000)+"ms");

        // Release kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

//        // Verify the result	- debil, blya! 
//        boolean passed = true;
//        final float epsilon = 1e-7f;
//        for (int i=0; i<n; i++)
//        {
//            float x = dstArray[i];
//            float y = srcArrayA[i] * srcArrayB[i];
//            boolean epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
//            if (!epsilonEqual)
//            {
//                passed = false;
//                break;
//            }
//        }
//        System.out.println("Test "+(passed?"PASSED":"FAILED"));
        if (n <= 1000)
        {
            System.out.println("Result: "+java.util.Arrays.toString(dstArray));
        }
    }
}