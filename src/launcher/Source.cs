using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Herbivore {

    class Launcher {

        static void Main(string[] args){
            Boolean window = args.Length > 0 && !args[0].Equals("showproc");
            string script;
            if (window){
                script = "herbivore-showproc.bat";
            }
            else{
                script = "herbivore-hideproc.vbs";
            }
            System.Diagnostics.Process.Start("lib\\" + script);
        }

    }

}
