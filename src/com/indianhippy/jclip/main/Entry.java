/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.indianhippy.jclip.main;

import javax.swing.SwingUtilities;
import com.indianhippy.jclip.scheduler.ClipAddingScheduler;
import com.indianhippy.jclip.ui.MainFrame;

/**
 *
 * @author achava
 */
public class Entry  {
    
    
    public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
            MainFrame frame = new MainFrame();
           ClipAddingScheduler cSche=new ClipAddingScheduler(frame,2);
          
            frame.pack();
            frame.setVisible(true);
            
        }
    });
    }
    
}
