import {useState,useEffect} from "react";

const ServerAddress = () => {
    const [envVariable, setEnvVariable] = useState<string|null>(null);

    useEffect(() => {
        const envVar = process.env.REACT_APP_MY_VARIABLE
        setEnvVariable(envVar || "");
    }, []);

    return envVariable;
};

export default ServerAddress;