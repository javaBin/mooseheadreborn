import {WorkshopInfoFromServer} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const readRegistrationFromServer = (registrationId: string):Promise<WorkshopInfoFromServer> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/registration/" + registrationId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            }).then(json => resolve(json))
            .catch(error => {
                reject(error.message);
            });
    });
};

export default readRegistrationFromServer;