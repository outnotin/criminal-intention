package com.augmentis.ayp.crimin;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;

import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Noppharat on 7/20/2016.
 */
public class CrimeLabTest {
    @Test
    public void test_create_crimeLab_no_error_and_size_100(){
        CrimeLab crimeLab = CrimeLab.getInstance(null);
//        assertEquals(crimeLab.getCrimes().size(), 99);

        for(int i=0; i<200;i++) {
            Crime crime = crimeLab.getCrimeById(UUID.randomUUID());
            assertNull(crime);
        }
    }

    @Test
    public void testArray(){
        int[] arr = new int[20];

        for(int i = 0 ; i < 200 ; i++){
            try{
                arr[i] = i;
            }catch (ArrayIndexOutOfBoundsException aoe){
                System.out.println("add more capacity when array is out of bound: " + arr.length );
                //ขยายขนาด array
                arr = Arrays.copyOf(arr, i + 50);
                arr[i] = i;
            }
        }

        System.out.print(arr.length);
    }


}
