use std::{env, thread};
use std::path::Path;
use std::time::Duration;

use j4rs::{ClasspathEntry, InvocationArg, JvmBuilder};

fn main() {
    env::set_var("JAVA_HOME", "jre");
    const CLASSPATH_ENTRY: &'static str = "roadwork.jar";
    if !Path::new(CLASSPATH_ENTRY).exists() {
        panic!("{} not found", CLASSPATH_ENTRY);
    }
    let entry = ClasspathEntry::new(CLASSPATH_ENTRY);
    println!("entry: {:?}", entry);
    match JvmBuilder::new()
        .classpath_entry(entry)
        .build() {
        Ok(jvm) => {
            println!("jvm started");
            let result = jvm.invoke_static(
                "com.kpouer.roadwork.RoadworkApplication",
                "start",
                InvocationArg::empty()
            ).unwrap();
            thread::sleep(Duration::from_secs(20));
        }
        Err(err) => {
            panic!("Failed to create JVM: {:?}", err);
        }
    }
}