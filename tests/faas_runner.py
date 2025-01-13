class FaasInstance:

    def stop(self):
        raise NotImplementedError()


class FaasRunner:

    def run(self, envs: dict):
        raise NotImplementedError()
