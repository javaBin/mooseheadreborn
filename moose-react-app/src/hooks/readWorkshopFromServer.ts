import {WorkshopType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

interface WorkshopFromServer {
    workshop: WorkshopType|null;
    errormessage: string|null;
}
// NOT USED
const readWorkshopFromServer =  (workshopId: string):Promise<WorkshopType> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/workshop/" + workshopId)//
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

    })
};

export default readWorkshopFromServer;
